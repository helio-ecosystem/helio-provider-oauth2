package provider;

import java.net.MalformedURLException;
import java.net.URL;

import com.github.scribejava.core.builder.api.DefaultApi20;

public class GenericAPI extends DefaultApi20{
	
	public URL tokenEndpoint;
	public URL authorizationEndpoint; 
	
	public GenericAPI() {
		
	}

	public void setTokenEndpoint(String tokenEndpoint) {
		try {
			this.tokenEndpoint = new URL(tokenEndpoint);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void setAuthorizationEndpoint(String authorizationEndpoint) {
		try {
			this.authorizationEndpoint = new URL(authorizationEndpoint);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private static class InstanceHolder{
		private static final GenericAPI INSTANCE = new GenericAPI();
	}
	
    public static GenericAPI instance() {
        return InstanceHolder.INSTANCE;
    }

	@Override
	public String getAccessTokenEndpoint() {
		return Tokens.URL_ENDPOINT;
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		return Tokens.BASE_URL;
	}

}
