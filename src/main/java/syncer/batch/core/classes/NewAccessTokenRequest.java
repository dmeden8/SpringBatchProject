package syncer.batch.core.classes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewAccessTokenRequest {
	
	private String refresh_token;
	private String client_id;
	private String client_secret;
	private String grant_type = "refresh_token";

}
