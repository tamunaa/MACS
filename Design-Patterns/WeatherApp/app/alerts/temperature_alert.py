import random
from typing import Tuple

from app.alerts.base_alert import BaseAlert
from app.weather_data import AlertType, WeatherData


class TemperatureAlert(BaseAlert):
    def __init__(self, threshold: float | None = None) -> None:
        super().__init__()
        self.alert_type = AlertType.TEMPERATURE
        self._threshold = threshold if threshold is not None else round(
            random.uniform(30.0, 35.0), 1)

    def _check_alert_conditions(self, weather_data: WeatherData) -> Tuple[bool, str]:
        if weather_data.temperature > self._threshold:
            return True, (f"Temperature exceeded {self._threshold}°C: "
                          f"{weather_data.temperature}°C")
        return False, ""
