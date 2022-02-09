package provider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.JsonObject;

import helio.blueprints.components.DataProvider;

public class Oauth2Provider implements DataProvider {

	private static final long serialVersionUID = 1L;
	private String api;
	private String api_Secret;
	private String server;
	private String user;
	private String pass;
	private String device_action;

	private OAuth20Service service;
	private Response response;
	private OAuth2AccessToken token;
	private OAuthRequest request;
	static Logger logger = LoggerFactory.getLogger(Oauth2Provider.class);

	public Oauth2Provider() {
		super();
	}

	/**
	 * arg0: API
	 * arg1: API Secret
	 * arg2: server
	 * arg3: user
	 * arg4: pass
	 * arg5: device_action
	 */
	@Override
	public void configure(JsonObject arg0) {
		if(arg0.size() == 6) {
			if(arg0.get(Tokens.API) != null) {
				api = arg0.get(Tokens.API).getAsString();
			}
			if(arg0.get(Tokens.API_SECRET) != null) {
				api_Secret = arg0.get(Tokens.API_SECRET).getAsString();
			}
			if(arg0.get(Tokens.SERVER_URL) != null) {
				server = arg0.get(Tokens.SERVER_URL).getAsString();
			}
			if(arg0.get(Tokens.USERNAME) != null) {
				user = arg0.get(Tokens.USERNAME).getAsString();
			}
			if(arg0.get(Tokens.PASSWORD) != null) {
				pass = arg0.get(Tokens.PASSWORD).getAsString();
			}
			if(arg0.get(Tokens.DEVICE_ACT) != null) {
				device_action = arg0.get(Tokens.DEVICE_ACT).getAsString();
			}
			setConnection();
		}else {
			logger.error("aaa");
		}
	}

	public Response setConnection() {
		try {
			service = new ServiceBuilder(api).apiSecret(api_Secret).build(GenericAPI.instance());
			token = service.getAccessTokenPasswordGrant(user, pass, device_action);		
			request = new OAuthRequest(Verb.GET, server);
			request.addHeader("Content-Type", "application/json");
			service.signRequest(token, request);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return response;
	}

	public void refreshToken() {
		try {
			service.refreshAccessToken(token.getRefreshToken());
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	public InputStream receiveData() {
		try {
			return new ByteArrayInputStream(response.getBody().getBytes());
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}

	@Override
	public InputStream getData() {
		try {
			response = service.execute(request);
			if(response.isSuccessful()) {
				return receiveData();
			}else {
				refreshToken();
				return receiveData();
			}
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}

}
