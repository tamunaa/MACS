import abc
import random

from app.weather_data import WeatherData


class WeatherGenerator(abc.ABC):
    @abc.abstractmethod
    def generate_weather(self, current: WeatherData, week: int) -> WeatherData:
        pass


class SimpleWeatherGenerator(WeatherGenerator):
    def generate_weather(self, current: WeatherData, week: int) -> WeatherData:
        if week <= 4:
            temperature = current.temperature + 2.0
            humidity = current.humidity + 2.0
            wind_speed = current.wind_speed + 3.0
        else:
            temperature = current.temperature
            humidity = current.humidity
            wind_speed = current.wind_speed

        temperature = max(10.0, min(45.0, temperature))
        humidity = max(40.0, min(95.0, humidity))
        wind_speed = max(0.0, min(40.0, wind_speed))

        temperature = round(temperature, 1)
        humidity = round(humidity, 1)
        wind_speed = round(wind_speed, 1)

        return WeatherData(temperature, humidity, wind_speed)


class RandomWeatherGenerator(WeatherGenerator):

    def generate_weather(self, current: WeatherData, week: int) -> WeatherData:

        temperature = current.temperature + random.uniform(-3.0, 5.0)
        humidity = current.humidity + random.uniform(-8.0, 10.0)
        wind_speed = current.wind_speed + random.uniform(-4.0, 6.0)

        temperature = max(10.0, min(45.0, temperature))
        humidity = max(40.0, min(95.0, humidity))
        wind_speed = max(0.0, min(40.0, wind_speed))

        temperature = round(temperature, 1)
        humidity = round(humidity, 1)
        wind_speed = round(wind_speed, 1)

        return WeatherData(temperature, humidity, wind_speed)


class CompositeWeatherGenerator(WeatherGenerator):
    def __init__(self) -> None:
        self._simple_generator = SimpleWeatherGenerator()
        self._random_generator = RandomWeatherGenerator()

    def generate_weather(self, current: WeatherData, week: int) -> WeatherData:
        if week <= 3:
            return self._simple_generator.generate_weather(current, week)
        else:
            return self._random_generator.generate_weather(current, week)

