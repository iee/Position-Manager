package position;


/*
 * TODO (tema): 1) Read properties from configuration file with Properties
 * class.
 */

/**
 * This interface stores common properties.
 * 
 */
public interface IConfiguration {

	/** Sequence the embedded partition begins with. */
	public static final String EMBEDDED_REGION_BEGINS = "/* <---";

	/** Sequence the embedded partition ends with. */
	public static final String EMBEDDED_REGION_ENDS = "/* ---> */";
}
