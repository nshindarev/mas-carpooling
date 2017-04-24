package dreamteam.carpooling.appl.Util;

/**
 * Константы, используемые при коммуникации
 */
public class Conversation {
    public static final String CARPOOLING_ONTOLOGY = "carpooling";

    public static final String CONTENT_STUB = "stub";
    public static final String NO_SEATS = "no-seats";
    public static final String NOT_FOUND_DRIVER = "fail";

    public static final int REPLY_TIME = 2000;

    public static final double START_PRICE = 10;
    public static final double PRICE_STEP = 100;

    public static final int MAX_ITER_WITH_NO_ANSWERS = 30;

    public static final String SECRETARY_NAME = "secretary";

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
