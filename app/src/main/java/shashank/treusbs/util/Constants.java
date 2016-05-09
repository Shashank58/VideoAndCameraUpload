package shashank.treusbs.util;

/**
 * Created by shashankm on 30/04/16.
 */
public class Constants {
    public static final String CREATE_VIDEO_URL = "http://androidvideo.herokuapp.com/api/create_video";
    public static final String GET_ALL_VIDEOS_URL = "http://androidvideo.herokuapp.com/api/home";
    public static final String GET_INDIVIDUAL_VIDEO_URL = "http://androidvideo.herokuapp.com/api/video";

    public static final String REMOTE_VIDEO_BASE_HTML = "<!DOCTYPE HTML> <html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:og=\"http://opengraphprotocol.org/schema/\"> <head></head> <body style=\"margin:0 0 0 0; padding:0 0 0 0;\"> <iframe width=\"100%\" height=\"275\" src=\"";
    public static final String REMOTE_VIDEO_END_HTML = "\" frameborder=\"0\" webkitAllowFullScreen mozallowfullscreen allowfullscreen></iframe> </body> </html> ";
}
