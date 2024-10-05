from app.weather_data import Observer, WeatherData


class WeatherDisplay(Observer):
    def update(self, weather_data: WeatherData) -> None:
        print(f"WeatherDisplay: Showing Temperature = {weather_data.temperature}°C, "
              f"Humidity = {weather_data.humidity}%, Wind Speed = "
              f"{weather_data.wind_speed} km/h")
