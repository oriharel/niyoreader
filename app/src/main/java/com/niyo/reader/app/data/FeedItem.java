package com.niyo.reader.app.data;

/**
 * Created by oriharel on 6/15/14.
 */
public class FeedItem {
    private String feedTitle;
    private String feedLink;
    private String feedGuid;
    private String feedPubDate;
    private String feedAuthor;
    private String feedDescription;
    private String feedEnclosureUrl;
    private String feedContent;

    public String getFeedTitle() {
        return feedTitle;
    }

    public void setFeedTitle(String feedTitle) {
        this.feedTitle = feedTitle;
    }

    public String getFeedLink() {
        return feedLink;
    }

    public void setFeedLink(String feedLink) {
        this.feedLink = feedLink;
    }

    public String getFeedGuid() {
        return feedGuid;
    }

    public void setFeedGuid(String feedGuid) {
        this.feedGuid = feedGuid;
    }

    public String getFeedPubDate() {
        return feedPubDate;
    }

    public void setFeedPubDate(String feedPubDate) {
        this.feedPubDate = feedPubDate;
    }

    public String getFeedAuthor() {
        return feedAuthor;
    }

    public void setFeedAuthor(String feedAuthor) {
        this.feedAuthor = feedAuthor;
    }

    public String getFeedDescription() {
        return feedDescription;
    }

    public void setFeedDescription(String feedDescription) {
        this.feedDescription = feedDescription;
    }

    public String getFeedEnclosureUrl() {
        return feedEnclosureUrl;
    }

    public void setFeedEnclosureUrl(String feedEnclosureUrl) {
        this.feedEnclosureUrl = feedEnclosureUrl;
    }

    public String getFeedContent() {
        return feedContent;
    }

    public void setFeedContent(String feedContent) {
        this.feedContent = feedContent;
    }
}
