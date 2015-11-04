package in.buzzzz.model;

/**
 * Created by Navkrishna on November 04, 2015
 */
public class Config extends Model {

    private Url url;

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public static class Url {
        private String api;
        private String chat;

        public String getApi() {
            return api;
        }

        public void setApi(String api) {
            this.api = api;
        }

        public String getChat() {
            return chat;
        }

        public void setChat(String chat) {
            this.chat = chat;
        }
    }
}
