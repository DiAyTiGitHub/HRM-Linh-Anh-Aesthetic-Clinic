package com.globits.hr.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globits.hr.dto.loginkeycloak.AccessDto;
import com.globits.hr.dto.loginkeycloak.CredentialDto;
import com.globits.hr.dto.loginkeycloak.UserKeyCloackDto;
import com.globits.timesheet.dto.TimeSheetDetailDto;
import com.globits.timesheet.dto.api.SearchTimeSheetApiDto;
import com.globits.timesheet.dto.api.TimeSheetRecordDto;
import com.globits.timesheet.dto.api.TimeSheetResponseDto;
import com.globits.timesheet.service.TimeSheetDetailService;

public class RestApiUtils {
	public static String host = "";
	public static String port = "";
	public static String api = "";
	public static String access_token = "";
	public static String token_type = "";
	public static String url = host + ":" + port + api;
	public static final ObjectMapper objectMapper = new ObjectMapper();
	public TimeSheetDetailService timeSheetDetailService;

	public static ResponseEntity<String> postLogin(String username, String password, String url) {
		try {
			HttpHeaders headersLogin = new HttpHeaders();
			headersLogin.add("Content-Type", "application/x-www-form-urlencoded");
			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("client_id", "admin-cli");
			map.add("grant_type", "password");
			map.add("username", username);
			map.add("password", password);
			map.add("scope", "openid");
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headersLogin);
			RestTemplate restTemplate = new RestTemplate();

			try {
				ResponseEntity<String> responseLogin = restTemplate.exchange(url, HttpMethod.POST, request,
						String.class);
				System.out.println(responseLogin.getStatusCodeValue());

				if (responseLogin.getBody() != null && responseLogin.getBody().length() > 0) {
					if (responseLogin.getBody().contains(",")) {
						String[] output = responseLogin.getBody().split(",");
						if (output.length > 0) {
							for (String s : output) {
								if (s != null && s.contains(":")) {
									String[] acc = s.split(":");
									if (acc.length > 0) {
										for (int j = 0; j < acc.length; j++) {
											if (acc[j].contains("access_token")) {
												access_token = acc[j + 1];
												access_token = access_token.replace('"', ' ');
												access_token = access_token.replace('"', ' ');
											}
											if (acc[j].contains("token_type")) {
												token_type = acc[j + 1];
												token_type = token_type.replace('"', ' ');
												token_type = token_type.replace('"', ' ');
											}
										}
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println(e.getLocalizedMessage());
			}
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static <T> ResponseEntity<T> post(String username, String password, String urlLogin, String url,
			Object parameterObject, Class<T> returnType) {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		postLogin(username, password, urlLogin);
		headers.add("Authorization", RestApiUtils.token_type + " " + RestApiUtils.access_token);
		HttpEntity<T> entity = new HttpEntity<T>((T) parameterObject, headers);
		return restTemplate.exchange(url, HttpMethod.POST, entity, returnType);
	}
	public static HttpClient createUnsafeClient() throws Exception {
	    TrustManager[] trustAllCerts = new TrustManager[]{
	        new X509TrustManager() {
	            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
	            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
	            public X509Certificate[] getAcceptedIssuers() { return null; }
	        }
	    };

	    SSLContext sslContext = SSLContext.getInstance("TLS");
	    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

	    return HttpClient.newBuilder()
	            .sslContext(sslContext)
	            .build();
	}

	public static TimeSheetResponseDto postApi(String url, Object requestDto) throws Exception {
//		HttpClient client = HttpClient.newHttpClient();
		HttpClient client = createUnsafeClient();

		String requestBody = objectMapper.writeValueAsString(requestDto);

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

//		return response.body(); // Trả lại kết quả dưới dạng chuỗi
		// Convert response JSON thành ChamCongResponse
		System.out.println("Response Body: " + response.body());
		TimeSheetResponseDto dto = objectMapper.readValue(response.body(), TimeSheetResponseDto.class);

//		List<TimeSheetRecordDto> list = dto.getTable1();
//		for (TimeSheetRecordDto record : list) {
//		    System.out.println(record.getTenNhanVien());
//		}
		return dto;
	}

	public static void main(String[] args) {
		UserKeyCloackDto dto = new UserKeyCloackDto();
		dto.setCreatedTimestamp(new Date());
		dto.setUsername("letung1");
		dto.setEnabled(true);
		dto.setTotp(false);
		dto.setEmailVerified(true);
		dto.setFirstName("le");
		dto.setLastName("tung1");
		dto.setEmail("letung1@gmail.com");
		dto.setDisableCredentialTypes(new ArrayList<>());
		dto.setRequiredActions(new ArrayList<>());
		dto.setNotBefore(0);
		dto.setAccess(new AccessDto());
		dto.getAccess().setImpersonate(true);
		dto.getAccess().setManage(true);
		dto.getAccess().setManageGroupMembership(true);
		dto.getAccess().setMapRoles(true);
		dto.getAccess().setView(true);
		dto.setRealmRoles(new ArrayList<>());
		dto.getRealmRoles().add("mb-user");
		dto.setCredentials(new ArrayList<>());
		CredentialDto cDto = new CredentialDto();
		cDto.setType("password");
		cDto.setValue("letung2");
//        postLogin("admin", "admin", "http://gcom.globits.net:8073/auth/realms/master/protocol/openid-connect/token");
//        System.out.print(RestApiUtils.token_type + " " + RestApiUtils.access_token);
//        HttpHeaders headers = new HttpHeaders();
//        ResponseEntity<UserKeyCloackDto> s = post("admin", "admin", "http://gcom.globits.net:8073/auth/realms/master/protocol/openid-connect/token", "http://gcom.globits.net:8073/auth/admin/realms/globits/users", dto, UserKeyCloackDto.class);
//        System.out.print(s);
		com.globits.timesheet.dto.api.SearchTimeSheetApiDto dtoApi = new SearchTimeSheetApiDto();
		dtoApi.setFromdate("2025-03-01");
		dtoApi.setTodate("2025-03-31");
		try {
			TimeSheetResponseDto apitimeSheet = postApi(
					"https://prod-61.southeastasia.logic.azure.com:443/workflows/4ebd4d253f894a2aad3944a7e60958b5/triggers/manual/paths/invoke?api-version=2016-06-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=sEsxMu-7IT0YGy3cFE9qgb9p6vsCNONlKFcOZagG6Vo",
					dtoApi);			
			System.out.print("ok_" +apitimeSheet.getTable1().size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
