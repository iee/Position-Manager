package position;

/**
 * This interface stores common properties.
 * 
 */
public interface IConfiguration {

	public static final String PARTITIONING_ID = "__iee_embedding_partitioning";
	
	/** Content type of embedded partition. */
	public static final String CONTENT_TYPE_EMBEDDED = "__embedded";

	/** Content type of plain text partition. */
	public static final String CONENT_TYPE_PLAINTEXT = "__plaintext";
	
	/** Sequence the embedded partition begins with. */
	public static final String EMBEDDED_REGION_BEGINS = "/*";

	/** Sequence the embedded partition ends with. */
	public static final String EMBEDDED_REGION_ENDS = "*/";
}
