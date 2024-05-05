package cz.forgottenempire.servermanager.workshop.metadata;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class HtmlPropertyProvider implements PropertyProvider {

    private static final String MOD_NAME_SELECTOR = ".workshopItemTitle";
    private static final String CONSUMER_APP_ID_ATTRIBUTE_KEY = "data-appid";
    private static final String CONSUMER_APP_ID_SELECTOR = "[" + CONSUMER_APP_ID_ATTRIBUTE_KEY + "]";

    private final Document document;

    HtmlPropertyProvider(Document document) {
        this.document = document;
    }

    @Override
    public String findName() {
        Element modNameElement = document.selectFirst(MOD_NAME_SELECTOR);
        return modNameElement == null ? null : modNameElement.text();
    }

    @Override
    public String findConsumerAppId() {
        Element consumerAppIdElement = document.selectFirst(CONSUMER_APP_ID_SELECTOR);
        return consumerAppIdElement == null ? null : consumerAppIdElement.attr(CONSUMER_APP_ID_ATTRIBUTE_KEY);
    }
}
