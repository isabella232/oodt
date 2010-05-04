//Copyright (c) 2007, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
//$Id$

package gov.nasa.jpl.oodt.cas.profile;

//CAS imports
import gov.nasa.jpl.oodt.cas.filemgr.structs.Element;
import gov.nasa.jpl.oodt.cas.filemgr.structs.Product;
import gov.nasa.jpl.oodt.cas.filemgr.structs.ProductType;
import gov.nasa.jpl.oodt.cas.filemgr.structs.Query;
import gov.nasa.jpl.oodt.cas.filemgr.structs.TermQueryCriteria;
import gov.nasa.jpl.oodt.cas.filemgr.system.XmlRpcFileManagerClient;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.jpl.oodt.cas.profile.util.ProfileUtils;

//JDK imports
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

//OODT imports
import jpl.eda.profile.Profile;
import jpl.eda.profile.ProfileException;
import jpl.eda.profile.handlers.ProfileHandler;
import jpl.eda.xmlquery.QueryElement;
import jpl.eda.xmlquery.XMLQuery;

/**
 * @author mattmann
 * @version $Revision$
 * 
 * <p>
 * An OODT {@link ProfileHandler} that queries a backend OODT File Management
 * component, and converts {@link Product}s and their {@link Metadata} into
 * OODT {@link Profile}s.
 * </p>
 */
public class CASProfileHandler implements ProfileHandler {

    /* our log stream */
    private static final Logger LOG = Logger.getLogger(CASProfileHandler.class
            .getName());

    /* our filemgr client */
    private XmlRpcFileManagerClient fmClient = null;

    /* the base URL to the data delivery servlet for products */
    /* AKA our "web-ified" product server */
    private String dataDelivBaseUrlStr = null;

    /* product type filter: a list of product types to query across */
    private List productTypeFilter = null;

    public CASProfileHandler() throws InstantiationException {
        // need to read in the system property
        // telling us which filemgr to communicate with

        String filemgrUrlStr = System.getProperty(
                "gov.nasa.jpl.oodt.cas.profile.fmUrl", "http://localhost:9000");

        try {
            fmClient = new XmlRpcFileManagerClient(new URL(filemgrUrlStr));
        } catch (Exception e) {
            throw new InstantiationException(
                    "Error communicating with the filemgr: [" + filemgrUrlStr
                            + "]: message: " + e.getMessage());
        }

        // read in data deliv base url
        dataDelivBaseUrlStr = System.getProperty(
                "gov.nasa.jpl.oodt.cas.profile.dataDelivBaseUrl",
                "http://localhost:8080/filemgr/data");

        productTypeFilter = buildFilter(System
                .getProperty("gov.nasa.jpl.oodt.cas.profile.product.types"));

    }

    /*
     * (non-Javadoc)
     * 
     * @see jpl.eda.profile.handlers.ProfileHandler#findProfiles(jpl.eda.xmlquery.XMLQuery)
     */
    public List findProfiles(XMLQuery query) throws ProfileException {
        List profs = new Vector();

        if (productTypeFilter != null && productTypeFilter.size() > 0) {
            for (Iterator i = productTypeFilter.iterator(); i.hasNext();) {
                ProductType type = (ProductType) i.next();
                Query cQuery = convertQuery(query);

                profs.addAll(queryAndBuildProfiles(type, cQuery));
            }
        }

        return profs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jpl.eda.profile.handlers.ProfileHandler#get(java.lang.String)
     */
    public Profile get(String arg0) throws ProfileException {
        throw new ProfileException("Method not implemented yet");
    }

    /*
     * (non-Javadoc)
     * 
     * @see jpl.eda.profile.handlers.ProfileHandler#getID()
     */
    public String getID() {
        return "CAS Filemgr Profile Handler";
    }

    private List safeGetProductTypes() {
        List types = null;

        try {
            types = fmClient.getProductTypes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return types;
    }

    private List safeGetProducts(ProductType type) {
        List products = null;

        try {
            products = fmClient.getProductsByProductType(type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

    private ProductType safeGetProductTypeByName(String name) {
        ProductType type = null;

        try {
            type = fmClient.getProductTypeByName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return type;
    }

    private Metadata safeGetMetadata(Product p) {
        Metadata met = null;

        try {
            met = fmClient.getMetadata(p);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return met;
    }

    private List safeGetProductReferences(Product p) {
        List references = null;

        try {
            references = fmClient.getProductReferences(p);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return references;
    }

    private Element safeGetElementByName(String name) {
        Element elem = null;

        try {
            elem = fmClient.getElementByName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return elem;
    }

    private List buildFilter(String productTypeNames) {
        List typeFilter = new Vector();
        if (productTypeNames == null) {
            typeFilter = safeGetProductTypes(); // just grab them all
        } else {
            // split the string on ","
            String[] typeNames = productTypeNames.split(",");

            if (typeNames != null) {
                for (int i = 0; i < typeNames.length; i++) {
                    ProductType type = safeGetProductTypeByName(typeNames[i]);
                    typeFilter.add(type);
                }
            } else {
                LOG.log(Level.WARNING,
                        "Unable to parse comma delimited type string: ["
                                + productTypeNames + "]: using all types");
                typeFilter = safeGetProductTypes();
            }
        }

        return typeFilter;
    }

    private Query convertQuery(XMLQuery xmlQuery) {
        // here's the dumbed down algorithm
        // iterate over the whereSet
        // when you find a elemName, grab the next query element
        // ensure it's a literal, if so, there's your term query

        // that's all we'll support for now
        Query query = new Query();

        if (xmlQuery.getWhereElementSet() != null
                && xmlQuery.getWhereElementSet().size() > 0) {
            for (Iterator i = xmlQuery.getWhereElementSet().iterator(); i
                    .hasNext();) {
                QueryElement elem = (QueryElement) i.next();
                if (elem.getRole().equals("elemName")) {
                    String elemName = elem.getValue();

                    // to get the elem value, we need to grab the next elem
                    // ensure it's a literal, and then grab it
                    QueryElement litElem = (QueryElement) i.next();
                    if (!litElem.getRole().equals("LITERAL")) {
                        LOG.log(Level.WARNING,
                                "next element not literal: element: ["
                                        + litElem + "]: malformed xml query!");
                        break;
                    }
                    String elemValue = litElem.getValue();
                    TermQueryCriteria crit = new TermQueryCriteria();
                    crit.setElementName(elemName);
                    crit.setValue(elemValue);
                    query.addCriterion(crit);
                }
            }
        }

        return query;
    }

    private List queryAndBuildProfiles(ProductType type, Query query) {
        List profiles = new Vector();

        List products = null;

        try {
            products = fmClient.query(query, type);

            if (products != null && products.size() > 0) {
                for (Iterator i = products.iterator(); i.hasNext();) {
                    Product p = (Product) i.next();
                    p.setProductReferences(safeGetProductReferences(p));
                    Metadata met = safeGetMetadata(p);
                    try {
                        profiles.add(ProfileUtils.buildProfile(p, met,
                                dataDelivBaseUrlStr));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE,
                    "Exception querying the file manager for products: Message: "
                            + e.getMessage());
        }

        return profiles;

    }
}
