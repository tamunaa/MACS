# Assignment 3

## Overview

We need a **Weather Monitoring System** to help us keep track of weather conditions like temperature, humidity, and wind speed. The system should allow various parts of the system to respond automatically when the weather changes. For example, if the temperature gets too hot, an alert might be triggered. The system should also be able to add new parts easily without changing the main system.

Your goal is to create a system that can handle weather updates and notify different parts of the system that need to know about the changes.

## What You Need to Do

1. **WeatherStation (The Main System)**:
   - This part will store and update the weather data, including temperature, humidity, and wind speed.
   - It will also be responsible for sending the updated weather information to the different parts of the system that need to know when the weather changes.

2. **Parts of the System (Things That React to Weather Changes)**:
   - These parts will perform specific actions when the weather changes. Each part is responsible for reacting to certain weather data:
     - **WeatherDisplay**: This part simply shows the current weather data.
     - **TemperatureAlert**: This part will send an alert if the temperature becomes too high. The alert will be triggered when the temperature exceeds a randomly generated threshold (e.g., 32°C).
     - **WindSpeedAlert**: This part will issue an alert if it detects an upward trend in wind speed. Specifically, it will issue an alert if the wind speed has been increasing over the last couple of updates. The alert message should indicate the change in wind speed between the current and previous updates. For example, if the wind speed was 12 km/h, then 15 km/h in the previous update, and 18 km/h in the current update, the alert should be triggered, indicating that the wind speed is increasing.
     - **HumidityAlert**: This part will send an alert if the humidity is too high. The alert will be triggered when the humidity exceeds a randomly generated threshold (e.g., 75%).

Each of these parts will react differently depending on the type of weather data they care about.

## How the System Works

The **WeatherStation** will update the weather data periodically. When this happens, it will notify all the parts of the system. Each part will then decide what to do with the updated weather information. Some parts might display the weather, while others might send alerts if certain conditions are met.

## Steps to Follow

1. **Create the WeatherStation**:
   - Build a system that can store the weather data and send updates to the parts of the system that need to know about changes.

2. **Build the Parts of the System**:
   - Create different parts that will react to weather updates. Each part will care about different things, like temperature, wind speed, or humidity. These parts need to be flexible enough to handle any weather update they receive.

3. **Add Randomized Thresholds**:
   - Each part should have its own conditions (thresholds) that trigger an alert. For example, a **TemperatureAlert** might only send an alert if the temperature goes above a certain level, like 32°C. The threshold should be generated randomly so that each part behaves in a unique way.

4. **Simulate Weather Changes**:
   - Periodically update the **WeatherStation** with random values that make sense for temperature, humidity, and wind speed. When the weather data changes, it will notify all the parts of the system, and each part will act based on its logic.

5. **Allow for Adding and Removing Parts**:
   - The **WeatherStation** should be able to add new parts to the system as the simulation runs. If you want to remove a part, it should no longer get updates from the **WeatherStation**.

6. **Gradually Add More Parts**:
   - Over time, new parts should be added to the system randomly. Each new part should start receiving weather updates immediately after it is added.

7. **Run the Simulation**:
   - The simulation should include at least 20 updates. For the first few updates, the weather data can be set to fixed values. Later updates should have random values for temperature, humidity, and wind speed. Each part will respond to these updates based on its unique logic.

## Example Simulation Output:

```
Week 1:
WeatherDisplay: Showing Temperature = 28°C, Humidity = 70%, Wind Speed = 12 km/h

---
Week 2:
WeatherDisplay: Showing Temperature = 30°C, Humidity = 72%, Wind Speed = 15 km/h

---
Week 3:
WeatherDisplay: Showing Temperature = 32°C, Humidity = 74%, Wind Speed = 18 km/h

---
Week 4:
Adding: TemperatureAlert
WeatherDisplay: Showing Temperature = 36°C, Humidity = 80%, Wind Speed = 22 km/h
TemperatureAlert: **Alert! Temperature exceeded 32°C: 36°C**

---
Week 5:
Adding: WindSpeedAlert
WeatherDisplay: Showing Temperature = 40°C, Humidity = 65%, Wind Speed = 25 km/h
TemperatureAlert: **Alert! Temperature exceeded 32°C: 40°C**
WindSpeedAlert: **Alert! Wind speed is increasing: 22 km/h → 25 km/h**

---
Week 6:
Adding: HumidityAlert
WeatherDisplay: Showing Temperature = 45°C, Humidity = 90%, Wind Speed = 30 km/h
TemperatureAlert: **Alert! Temperature exceeded 32°C: 45°C**
WindSpeedAlert: **Alert! Wind speed is increasing: 25 km/h → 30 km/h**
HumidityAlert: **Alert! Humidity exceeded 85%: 90%**

---
Week 7:
WeatherDisplay: Showing Temperature = 43°C, Humidity = 92%, Wind Speed = 32 km/h
TemperatureAlert: **Alert! Temperature exceeded 32°C: 43°C**
WindSpeedAlert: **Alert! Wind speed is increasing: 30 km/h → 32 km/h**
HumidityAlert: **Alert! Humidity exceeded 85%: 92%**

---
Week 8:
WeatherDisplay: Showing Temperature = 40°C, Humidity = 85%, Wind Speed = 30 km/h
TemperatureAlert: **Alert! Temperature exceeded 32°C: 40°C**
WindSpeedAlert: No alert (No upward trend detected)
HumidityAlert: **Alert! Humidity exceeded 85%: 85%**
Removing: HumidityAlert

---
Week 9:
WeatherDisplay: Showing Temperature = 38°C, Humidity = 82%, Wind Speed = 28 km/h
TemperatureAlert: **Alert! Temperature exceeded 32°C: 38°C**
WindSpeedAlert: No alert (No upward trend detected)

---
Week 10:
WeatherDisplay: Showing Temperature = 36°C, Humidity = 80%, Wind Speed = 25 km/h
TemperatureAlert: **Alert! Temperature exceeded 32°C: 36°C**
WindSpeedAlert: No alert (No upward trend detected)
```

## Linting/formatting

- Format and lint your code using `ruff`
- Check your static types with `mypy`

## Testing

Provide automated tests that will falsify regressions (change in behaviour) in your software artifacts.

## Grading

We will not grade solutions:
  - without decomposition
  - with needlessly long methods or classes
  - with code duplications

In all these cases you will automatically get 0% so, we sincerely ask you to 
not make a mess of your code and not put us in an awkward position.

Grade breakdown:
  - 30%: It is tested.
  - 30%: It is easy to change.
  - 30%: It demonstrates an understanding of design patterns/principals.
  - 10%: It follows linting/formatting rules.

## Disclaimer

We reserve the right to resolve ambiguous requirements (if any) as we see fit just like a real-life stakeholder would.
So, do not assume anything, ask for clarifications.

## Run
``
python -m main
``

``
python -m pytest tests
``