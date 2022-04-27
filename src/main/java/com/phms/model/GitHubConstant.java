package com.phms.model;

/**
 * @Author Sijie He
 * @Date 2022/4/20 17:12
 * @Version 1.0
 */
public class GitHubConstant {
    // 这里填写在GitHub上注册应用时候获得 CLIENT ID
    public static final String  CLIENT_ID="0aa5d18a2e3feb816139";
    //这里填写在GitHub上注册应用时候获得 CLIENT_SECRET
    public static final String CLIENT_SECRET="ed3790e8fb3a0bfb56740f0febc0442f943b44db";
    // 回调路径
    public static final String CALLBACK = "http://localhost:8086/callback";

    //获取code的url
    public static final String CODE_URL = "https://github.com/login/oauth/authorize?client_id="+CLIENT_ID+"&state=STATE&redirect_uri="+CALLBACK+"";
    //获取token的url
    public static final String TOKEN_URL = "https://github.com/login/oauth/access_token?client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&code=CODE&redirect_uri="+CALLBACK+"";
    //获取用户信息的url
    public static final String USER_INFO_URL = "https://api.github.com/user?access_token=TOKEN";

}
