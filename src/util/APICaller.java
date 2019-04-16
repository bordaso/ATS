package util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import businessModels.Order;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import screens.TickerScreenLong;

public class APICaller {

	public static synchronized void callGetTickerData(TickerScreenLong screen) throws IOException {
		
		OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("Connection", "close").build();
                        return chain.proceed(request);
                    }
                })
                .build();

		SocketReader socketReader = new SocketReader(screen);
		HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:9966/Register").newBuilder();
		urlBuilder.addQueryParameter("symbol", screen.getTickerName().getText());
		urlBuilder.addQueryParameter("region", "1");
		urlBuilder.addQueryParameter("feedtype", "TOS");
		urlBuilder.addQueryParameter("output", socketReader.getPort().toString());
		urlBuilder.addQueryParameter("status", "on");
		String url = urlBuilder.build().toString();

		Request request = new Request.Builder().url(url).build();

		Response response;

		response = client.newCall(request).execute();

		if (response.isSuccessful()) {
			response.close();
			socketReader.connect();
		}

	}

	public static synchronized void callStopGetTickerData(TickerScreenLong screen) throws IOException {
		
		OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("Connection", "close").build();
                        return chain.proceed(request);
                    }
                })
                .build();

		SocketReader socketReader = new SocketReader(screen);
		HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:9966/Deregister").newBuilder();
		urlBuilder.addQueryParameter("symbol", screen.getTickerName().getText());
		urlBuilder.addQueryParameter("feedtype", "TOS");
		String url = urlBuilder.build().toString();

		Request request = new Request.Builder().url(url).build();

		Response response;

		response = client.newCall(request).execute();
		response.close();

	}

	public static synchronized void callSendOrder(List<? extends Order> orderList, String price, Side side)
			throws IOException {
		
		OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("Connection", "close").build();
                        return chain.proceed(request);
                    }
                })
                .build();

		for (Order order : orderList) {
			TextField[] fieldList = order.fieldList();

			Double sendingPrice = setupInPrice(side, price, fieldList[3].getText());

			System.out.println("Price "+price);
			System.out.println("Is modified by "+ side+ " " + fieldList[3].getText());
			System.out.println("To the price of " + sendingPrice);

			HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:9966/ExecuteOrder").newBuilder();
			urlBuilder.addQueryParameter("symbol", fieldList[0].getText());
			urlBuilder.addQueryParameter("limitprice", sendingPrice.toString());
			urlBuilder.addQueryParameter("ordername", fieldList[4].getText());
			urlBuilder.addQueryParameter("shares", fieldList[1].getText());
			
			
			
			if(integerStringConverter(fieldList[1].getText())>integerStringConverter(fieldList[2].getText())) {
			urlBuilder.addQueryParameter("displaysize", String.valueOf(integerStringConverter(fieldList[2].getText())));
			}
			
			String url = urlBuilder.build().toString();

			Request request = new Request.Builder().url(url).build();

			Response response;

			System.out.println("callSendOrder "+request);

			response = client.newCall(request).execute();

			response.close();
		}

	}

	public static synchronized void callCancelOrder(List<? extends Order> orderList, Side side) throws IOException {

		OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("Connection", "close").build();
                        return chain.proceed(request);
                    }
                })
                .build();
		

		Order order = orderList.get(0);
		TextField[] fieldList = order.fieldList();

		HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:9966/CancelOrder").newBuilder();
		urlBuilder.addQueryParameter("type", "all");
		urlBuilder.addQueryParameter("symbol", fieldList[0].getText());
		
		if (side.equals(Side.BUY)) {
			urlBuilder.addQueryParameter("side", "bid");
		} else if (side.equals(Side.SELL)) {
			urlBuilder.addQueryParameter("side", "ask");
		}
		
		String url = urlBuilder.build().toString();

		Request request = new Request.Builder().url(url).build();

		Response response;
		System.out.println("callCancelOrder "+ request);

		response = client.newCall(request).execute();
		response.close();

	}

	public static synchronized String callGetPositions(String user) throws IOException {
		
		OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("Connection", "close").build();
                        return chain.proceed(request);
                    }
                })
                .build();

		HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:9966/GetOpenPositions").newBuilder();
		urlBuilder.addQueryParameter("user", user);
		String url = urlBuilder.build().toString();

		Request request = new Request.Builder().url(url).build();

		Response response;
		System.out.println(request);

		response = client.newCall(request).execute();
		String responseBody = response.body().string();
		response.close();

		return responseBody;
	}



	private static Double setupInPrice(Side side, String price, String addedSubstractedBy) {

		Double value = new Double(0);
		
		if(addedSubstractedBy.indexOf(",")!=-1) {
			addedSubstractedBy=addedSubstractedBy.replaceAll(",","."); 
		}

		try {
		if (side.equals(Side.BUY)) {
			value = Double.valueOf(price) + Double.valueOf(addedSubstractedBy);
		} else if (side.equals(Side.SELL)) {
			value = Double.valueOf(price) - Double.valueOf(addedSubstractedBy);
		}
		}catch(Exception e) {
			value = Double.valueOf(price);
			Alert alertError = new Alert(AlertType.ERROR, "Tamas üzeni hogy hülye vagy.");
			alertError.show();
		}

		Double truncatedValue = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();

		return truncatedValue;
	}
	
	private static Response urlCaller(String... params) throws IOException {

		OkHttpClient client = new OkHttpClient();
		HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.github.help").newBuilder();

		for (int i = 0; i < params.length; i += 2) {
			urlBuilder.addQueryParameter(params[i], params[i + 1]);
		}

		String url = urlBuilder.build().toString();
		Request request = new Request.Builder().url(url).build();

		return client.newCall(request).execute();

	}
	
	private static int integerStringConverter(String in) {

		if(in==null) {
			
			return 0;
		}

		return in.isEmpty()?0:Integer.valueOf(in);

	}

	// public static void main(String[] args) {
	//
	// call();
	//
	// }

}
