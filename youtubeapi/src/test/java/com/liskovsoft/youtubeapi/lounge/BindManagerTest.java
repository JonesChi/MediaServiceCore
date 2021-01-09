package com.liskovsoft.youtubeapi.lounge;

import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor;
import com.liskovsoft.sharedutils.okhttp.OkHttpHelpers;
import com.liskovsoft.youtubeapi.common.helpers.RetrofitHelper;
import com.liskovsoft.youtubeapi.lounge.models.Command;
import com.liskovsoft.youtubeapi.lounge.models.CommandInfos;
import com.liskovsoft.youtubeapi.lounge.models.PairingCode;
import com.liskovsoft.youtubeapi.lounge.models.Screen;
import com.liskovsoft.youtubeapi.lounge.models.ScreenId;
import com.liskovsoft.youtubeapi.lounge.models.ScreenInfos;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;
import retrofit2.Call;

import javax.annotation.Nullable;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class BindManagerTest {
    private static final String SCREEN_NAME = "TubeNext";
    private static final String LOUNGE_TOKEN_TMP = "AGdO5p8cH1tKYW3OIVFhSMRfjAjV5OxqYdjCezBGrDAaX7be3bcttKQAVKucSpEcoi8qh6rYs_r04DXQhd0_xEZY69s8W5J7rqEMmeaYwJsSi5VivgnFKv4";
    private static final String SCREEN_ID_TMP = "910nbko7d2d6qtthu2609a3id6";
    private static final String BIND2_URL = "https://www.youtube.com/api/lounge/bc/bind?" +
            "device=LOUNGE_SCREEN&theme=cl&capabilities=dsp%2Cmic%2Cdpa&mdxVersion=2&VER=8&v=2&t=1" +
            "&RID=rpc&CI=0" +
            "&app=" + BindManagerParams.APP +
            "&id=" + BindManagerParams.SCREEN_UID +
            "&AID=" + BindManagerParams.AID +
            "&zx=" + BindManagerParams.ZX;
    private BindManager mBindManager;
    private ScreenManager mScreenManager;
    private CommandManager mCommandManager;

    @Before
    public void setUp() {
        // fix issue: No password supplied for PKCS#12 KeyStore
        // https://github.com/robolectric/robolectric/issues/5115
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");

        ShadowLog.stream = System.out; // catch Log class output

        mBindManager = RetrofitHelper.withRegExp(BindManager.class);
        mScreenManager = RetrofitHelper.withJsonPath(ScreenManager.class);
        mCommandManager = RetrofitHelper.withJsonPathSkip(CommandManager.class);
    }

    @Test
    public void testThatPairingCodeGeneratedSuccessfully() {
        Screen screen = getScreen();
        Call<PairingCode> pairingCodeWrapper = mBindManager.getPairingCode(BindManagerParams.ACCESS_TYPE, BindManagerParams.APP, screen.getLoungeToken(),
                screen.getScreenId(), SCREEN_NAME);
        PairingCode pairingCode = RetrofitHelper.get(pairingCodeWrapper);

        // Pairing code XXX-XXX-XXX-XXX
        assertNotNull("Pairing code not empty", pairingCode.getPairingCode());
    }

    @Test
    public void testThatFirstBindDataIsNotEmpty() {
        CommandInfos bindData = getFirstBind();

        assertNotNull("Contains bind data", bindData);
    }

    @Test
    public void testWebSocket() throws InterruptedException {
        CommandInfos firstBind = getFirstBind();
        Command command1 = firstBind.getCommands().get(0);
        Command command2 = firstBind.getCommands().get(1);
        String screenId = command1.getCommandParams().get(0);
        String sessionId = command2.getCommandParams().get(0);

        Request request = new Builder().url(String.format(
                "%s&name=%s&loungeIdToken=%s&SID=%s&gsessionid=%s",
                BIND2_URL, SCREEN_NAME, LOUNGE_TOKEN_TMP, screenId, sessionId
        )).build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //builder.addInterceptor(new OkHttpProfilerInterceptor());

        builder.addInterceptor(new Interceptor() {
            @Override public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .removeHeader("Upgrade")
                        .removeHeader("Connection")
                        .removeHeader("Sec-WebSocket-Key")
                        .removeHeader("Sec-WebSocket-Version")
                        .build();
                return chain.proceed(request);
            }
        });

        OkHttpClient okHttpClient = builder.build();

        //okHttpClient.networkInterceptors().add(new Interceptor() {
        //    @Override public Response intercept(Chain chain) throws IOException {
        //        Request request = chain.request()
        //                .newBuilder()
        //                .removeHeader("Upgrade")
        //                .removeHeader("Connection")
        //                .removeHeader("Sec-WebSocket-Key")
        //                .removeHeader("Sec-WebSocket-Version")
        //                .build();
        //        return chain.proceed(request);
        //    }
        //});

        okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
            }
        });

        Thread.sleep(100_000);
    }

    @Test
    public void testThatSecondBindDataIsNotEmpty() throws InterruptedException {
        CommandInfos firstBind = getFirstBind();
        Command command1 = firstBind.getCommands().get(0);
        Command command2 = firstBind.getCommands().get(1);
        String screenId = command1.getCommandParams().get(0);
        String sessionId = command2.getCommandParams().get(0);

        for (int i = 0; i < 10; i++) {
            Call<CommandInfos> bindDataWrapper = mCommandManager.bind2(SCREEN_NAME, LOUNGE_TOKEN_TMP, screenId, sessionId);

            CommandInfos bindData = RetrofitHelper.get(bindDataWrapper);

            //assertNotNull("Contains bind data", bindData);

            //Thread.sleep(10_000);
        }
    }

    private CommandInfos getFirstBind() {
        Call<CommandInfos> bindDataWrapper = mCommandManager.bind1(SCREEN_NAME, LOUNGE_TOKEN_TMP, 0);

        return RetrofitHelper.get(bindDataWrapper);
    }

    private Screen getScreen() {
        Call<ScreenId> screenIdWrapper = mBindManager.createScreenId();
        ScreenId screenId = RetrofitHelper.get(screenIdWrapper);

        Call<ScreenInfos> screenInfosWrapper = mScreenManager.getScreenInfos(screenId.getScreenId());
        ScreenInfos screenInfos = RetrofitHelper.get(screenInfosWrapper);

        return screenInfos.getScreens().get(0);
    }
}