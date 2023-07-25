package weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherApp {
	// Replace this URL with the actual weather API endpoint
	private static final String BASE_URL = "https://samples.openweathermap.org/data/2.5/forecast/hourly?q=London,us&appid=b6907d289e10d714a6e88b30761fae22";

	private static String sendGetRequest(String endpoint) throws IOException {
		URL url = new URL(endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			in.close();
			return response.toString();
		} else {
			return null;
		}
	}

	private static JSONObject getWeatherByDate(Date date) throws Exception {
		String response = sendGetRequest(BASE_URL);
		if (response != null) {
			JSONObject jsonObject = new JSONObject(response);
			JSONArray weatherList = jsonObject.getJSONArray("list");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for (int i = 0; i < weatherList.length(); i++) {
				JSONObject weatherData = weatherList.getJSONObject(i);
				String dtTxt = weatherData.getString("dt_txt");
				Date dateTxt = sdf.parse(dtTxt.substring(0, 10));

				if (sdf.format(date).equals(sdf.format(dateTxt))) {
					return weatherData;
				}
			}
		}

		throw new Exception("Weather data not found for the specified date: " + date);
	}

	private static void getWeather(Date date) throws Exception {

		JSONObject weatherData = getWeatherByDate(date);
		JSONArray weatherArray = weatherData.getJSONArray("weather");
		JSONObject weather = weatherArray.getJSONObject(0);
		String description = weather.getString("description");
		System.out.println("Weather Description: " + description);

	}

	private static void getWindSpeed(Date date) throws Exception {
		JSONObject weatherData = getWeatherByDate(date);
		JSONObject windData = weatherData.getJSONObject("wind");
		double windSpeed = windData.getDouble("speed");
		System.out.println("WindSpeed: " + windSpeed);
	}

	private static void getPressure(Date date) throws Exception {
		JSONObject weatherData = getWeatherByDate(date);
		JSONObject mainData = weatherData.getJSONObject("main");
		double pressure = mainData.getDouble("pressure");
		System.out.println("Pressure: " + pressure);
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		while (true) {
			System.out.println("1. Get weather");
			System.out.println("2. Get Wind Speed");
			System.out.println("3. Get Pressure");
			System.out.println("0. Exit");
			System.out.print("Enter your choice: ");
			int choice = scanner.nextInt();
			Date date=null;
			try {
				if (choice != 0) {
					System.out.println("Enter desired Date [yyyy-MM-dd]:");
					String dateString = scanner.next();
					  date = sdf.parse(dateString);
				}
				switch (choice) {
				case 1:
					getWeather(date);
					break;
				case 2:
					getWindSpeed(date);
					break;
				case 3:
					getPressure(date);
					break;
				case 0:
					System.out.println("Exiting...");
					scanner.close();
					return;
				default:
					System.out.println("Invalid choice. Please try again.");
				}
			} catch (Exception e) {
				System.out.println("Error " + e.getMessage());
			}
		}
	}
}
