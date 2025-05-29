# Arena

[![Build Status](https://github.com/kenya-jug/arena/actions/workflows/maven.yml/badge.svg)](https://github.com/kenya-jug/arena/actions/workflows/maven.yml)

<img src="https://github.com/user-attachments/assets/b005ee9a-2ebc-492d-8ba3-0ddb7a3ff39c" alt="Arena Logo" width="200" height="200"/>

Real-Time Multiplayer Game Server (with Betting Feature)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/MwauratheAlex/arena.git
   cd arena
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   The server will start on `http://localhost:8080`

## If using intellij hot-reloading
1. **Watch for tailwind changes on a separate terminal**
   ```bash
   cd arena/src/main/frontend
   npm i #install node modules if running for the first time
   npm run watch #to watch html files for changes and rebuild your css
   ```
2. **Run Normally on intellij**
- Tailwind will build automatically on save, and you only need to rerun your server on changes.
- If running templates directly with the intellij server and hot-reloading, 
tailwind will still build automatically on save, and you will see changes immediately on your browser.
- NOTE: mvn spring-boot:run will install packages and 
build tailwind so there is no need to run watch separately.


---






<!-- coverage start -->
## 📊 Code Coverage Report

**Overall Coverage: 32.46% ⚠️**

| Metric      | Covered | Missed | Total | Coverage |
|-------------|---------|--------|--------|----------|
| INSTRUCTION | 236 | 491 | 727 | 32.46% ⚠️ |
| LINE | 56 | 106 | 162 | 34.57% ⚠️ |
| BRANCH | 2 | 28 | 30 | 6.67% ⚠️ |
| METHOD | 22 | 28 | 50 | 44.00% ⚠️ |
| CLASS | 9 | 5 | 14 | 64.29% ⚠️ |
| COMPLEXITY | 23 | 42 | 65 | 35.38% ⚠️ 

### 🚨 Least Tested Elements (coverage below 50%)
- INSTRUCTION: 32.46%
- LINE: 34.57%
- BRANCH: 6.67%
- METHOD: 44.00%
- COMPLEXITY: 35.38%
<!-- coverage end -->
