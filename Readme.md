### Cách chạy frontend ###
cd Frontend
npm install
npm run dev   # chạy dev server
npm run build # build production ra thư mục dist

### Cách chạy code backend ###
cd Backend
mvn clean package -DskipTests
java -jar target/app.jar
Mặc định backend chạy ở http://localhost:8080

* Lưu ý trong trường hợp dùng windows thì chạy code như sau:
cd Backend
.\mvnw.cmd spring-boot:run

### Khi lấy source code thì nhớ lấy từ nhánh develop
git clone -b develop git@github.com:fathenot/Project-HK251.git
