package fr.mbds.securesms.proxy_for_emulator;

import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 *
 * ERROR : com.android.volley.NoConnectionError: java.io.IOException: Cleartext HTTP traffic to baobab.tokidev.fr not permitted
 *
 * https://stackoverrun.com/de/q/10725160
 */
public class ProxyHurlStack extends HurlStack {

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.1.17", 8888));
        return (HttpURLConnection) url.openConnection(proxy);//super.createConnection(url);
    }
}
