package dreamteam.carpooling.appl.Util;

/**
 * Константы, используемые при коммуникации
 */
public class Conversation {
    public static final String CARPOOLING_ONTOLOGY = "carpooling";

    public static final String CONTENT_STUB = "stub";

    public static final int REPLY_TIME = 10000;
    public static final double PRICE_STEP = 10;
    public static final int MAX_ITER_WITH_NO_ANSWERS = 30;

    private static int currentID = 0;

    public static String getNextID() {
        return "CONV".concat(String.valueOf(++currentID));
    }

    public static String convertProposalDataToContent(String start, String finish, double v) {
        return start + "," + finish + "," + v;
    }

    // TODO: implement
    public static void convertContentToProposalData(String content) {

    }
}
