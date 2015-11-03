package bml2;

import java.util.Scanner;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/*
 * シミュレーション結果を逐次Twitterに投稿して
 * リアルタイムに確認するためのクラス
 */

public class MyTwitter {
	private static Twitter twitter;

	public static void initialize() {
		twitter = new TwitterFactory().getInstance();

		String customerKey = "usBrapr6xIbitQyfbMdSpCM8p";
		String customerSecret = "80xoBvK4tqsbyvdZUw0h5mj43N7txjbiQquL8EllEjzBnf0Yu2";
		twitter.setOAuthConsumer(customerKey, customerSecret);

		RequestToken requestToken = null;
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch (IllegalStateException ie) {
			// リクエストトークンを取得する必要がない
			if (!twitter.getAuthorization().isEnabled()) {
				System.out.println("OAuth key is not set");
				System.exit(1);
			}
		} catch (TwitterException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		AccessToken accessToken = null;

		System.out.println("Grant access to your account: ");
		System.out.println(requestToken.getAuthenticationURL());
		System.out.println("PIN?: ");
		String pin = (new Scanner(System.in)).nextLine();

		try {
			accessToken = twitter.getOAuthAccessToken(requestToken, pin);
		} catch (TwitterException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		System.out.println("Twitter Client 初期化 正常終了");
	}

	public static void tweet(String str) {
		try {
			twitter.updateStatus(str);
		} catch (TwitterException e) {
			System.out.println(e.getMessage());
			System.out.println("ツイートできません");
		}
	}
}