import com.codahale.metrics.annotation.Timed
import com.google.api.client.auth.oauth2.Credential
import com.uber.sdk.core.auth.Scope
import com.uber.sdk.rides.auth.OAuth2Credentials
import com.uber.sdk.rides.client.CredentialsSession
import com.uber.sdk.rides.client.ServerTokenSession
import com.uber.sdk.rides.client.SessionConfiguration
import com.uber.sdk.rides.client.UberRidesApi
import com.uber.sdk.rides.client.model.UserProfile
import com.uber.sdk.rides.client.services.RidesService
import groovy.util.logging.Slf4j
import retrofit2.Response

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.QueryParam

@Path("/uber-req")
@Slf4j
class UberRequestResource {

    ServerTokenSession serverSession
    ApplicationConfiguration configuration

    // works for one user. replace with map of things / database thing
    OAuth2Credentials credentials
    SessionConfiguration userSessionConfig
    Credential credential


    UberRequestResource(ServerTokenSession session, ApplicationConfiguration configuration){
        this.serverSession = session
        this.configuration = configuration
    }

    /*
    @Path("/test")
    @GET
    @Timed
    String test(){
        def service = UberRidesApi.with(session).build().createService()
        Response response1 = service.getProducts(37.79f, -122.39f).execute()
        List<Product> products = response1.body().products
        def productId = products[0].productId
        Response<TimeEstimatesResponse> response2 = service.getPickupTimeEstimate(37.79f, -122.39f, productId).execute()
        return response2.body().times[0].estimate.toString()
    }
    */

    @Path("/get-auth-url")
    @GET
    @Timed
    String getAuthUrl(){
        userSessionConfig = new SessionConfiguration.Builder()
            .setClientId(configuration.uberClientId)
            .setClientSecret(configuration.uberSecret)
            .setEnvironment(SessionConfiguration.Environment.SANDBOX)
            .setScopes(Arrays.asList(Scope.PROFILE, Scope.REQUEST))
            .setRedirectUri("http://127.0.0.1:8080/uber-req/register")
            .build()

        credentials = new OAuth2Credentials.Builder()
            .setSessionConfiguration(userSessionConfig)
            .build()


        return credentials.getAuthorizationUrl()

    }

    @GET
    @Path("/register")
    @Timed
    javax.ws.rs.core.Response register(@QueryParam("code") String authCode){
        def userId = "testUserId"
        try{
            credential = credentials.authenticate(authCode,userId)
        } catch (Exception e){
            log.warn("Failed to authenticate user " + userId)
            return javax.ws.rs.core.Response.status(403).build()
        }
        return javax.ws.rs.core.Response.status(200).build()
    }

    @GET
    @Path("/test")
    String test(){
        CredentialsSession session = new CredentialsSession(userSessionConfig, credential)
        RidesService service = UberRidesApi.with(session).build().createService()
        Response<UserProfile> profileResponse = service.getUserProfile().execute()
        return profileResponse.body().email
    }

    @POST
    @Path("/gohome")
    javax.ws.rs.core.Response submit(){
        return javax.ws.rs.core.Response.status(403).build()
    }

}
