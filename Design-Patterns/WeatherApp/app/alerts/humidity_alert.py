import random
from typing import Tuple

from app.alerts.base_alert import BaseAlert
from app.weather_data import AlertType, WeatherData


class HumidityAlert(BaseAlert):
    def __init__(self, threshold: float | None = None) -> None:
        super().__init__()
        self.alert_type = AlertType.HUMIDITY
        self._threshold = round(
            threshold if threshold is not None else random.uniform(75.0, 85.0), 1)

    def _check_alert_conditions(self, weather_data: WeatherData) -> Tuple[bool, str]:
        if weather_data.humidity >= self._threshold:
            return True, (f"Humidity exceeded {self._threshold}%: "
                          f"{weather_data.humidity}%")
        return False, ""
