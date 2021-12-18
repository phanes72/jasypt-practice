# jasypt-practice

Tutorial Followed: https://medium.com/@javatechie/spring-boot-password-encryption-using-jasypt-e92eed7343ab



In this tutorial, I will guide you how to encrypt sensitive information in Spring Boot application configuration file (application.properties or application.yml), such as username and password of a datasource, credentials of SMTP server, etc… using Jasypt library
Jasypt is a Java library which helps developers with basic encryption in configurations without much effort or in-depth knowledge of encryption implementation. Please go through http://www.jasypt.org/ for detailed information.
Let us consider a scenario where we build a spring boot CRUD application , We will need to store information like database connection details or any other sensitive information which will make it vulnerable.
To avoid this vulnerability issue its recommended to encrypt sensitive information in your config file .
Steps To Add Encryption Using Jasypt:
1. Add maven dependency of jasypt: In the pom.xml file, add maven dependency which can be found easily at Maven Repository
If you want to simplify encryption & decryption process then you can add below maven plugin (we will follow the steps using maven plugin)
<plugin>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-maven-plugin</artifactId>
    <version>3.0.3</version>
</plugin>
2. Add annotation in the Spring Boot Application main Configuration class: @EnableEncryptableProperties annotation needs to be added to make the application understand the encryptable properties across the entire Spring Environment
3. Decide a secret key to be used for encryption and decryption The secret key is used to encrypt the password and later can be used to decrypt the encrypted value to get the actual password. You can choose any value as the secret key.
4. Encrypt and Decrypt single String value
Open a new command prompt window. Change the current directory to the project directory where the pom.xml file is in. And type the following command:
mvn jasypt:encrypt-value -Djasypt.encryptor.password=javatechie -Djasypt.plugin.value=Password
input: Password (Actual password to be encrypted)
password: javatechie (the secret key chosen by you)
algorithm: PBEWithMD5AndDES (default algorithm used)
you would see it prints something like this:
ENC(nObqvmVPYhxVaykMl09QVtGCQWjpd7al1RJhOsyz1eLkb6J2USMu9Fb//e4a6Vro) (Encrypted value of input)

If you run the above command again, you will see a different encrypted value because the default encryptor uses a random generator. That means a string can be different encrypted value though the private key is the same
The default encrypt algorithm is bidirectional, which means you can do decryption. Type the following command:
mvn jasypt:decrypt-value -Djasypt.encryptor.password=javatechie -Djasypt.plugin.value=nObqvmVPYhxVaykMl09QVtGCQWjpd7al1RJhOsyz1eLkb6J2USMu9Fb//e4a6Vro

So these encrypt and decrypt commands are the very basic ones you should be familiar with.
Now once you verified your encryption key is correct then you can directly add your encrypted key in application config file like below

Now, to run the Spring Boot application you need to pass the private key password as VM arguments in the command prompt like this:
java -Djasypt.encryptor.password=javatechie -jar spring-boot-application.jar
To run the Spring Boot application in Eclipse or intellij idea IDE, you need to edit the run configuration by passing a VM argument like this:

5. Encrypt credential in application.properties file
Suppose that you want to encrypt username and password of a Spring data source in the following application.properties file:
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url = jdbc:mysql://localhost:3306/javatechie
spring.datasource.username = root
spring.datasource.password = Password
First, wrap the values password inside DEC() as shown below:
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url = jdbc:mysql://localhost:3306/javatechie
spring.datasource.username = root
spring.datasource.password = DEC(Password)
Here, DEC() is a placeholder that tells Jasypt what to encrypt, and the remaining values are untouched.
And in the command prompt, type:
mvn jasypt:encrypt -Djasypt.encryptor.password=javatechie
Then it will replace the DEC() placeholders in the application.properties file with the encrypted value:
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url = jdbc:mysql://localhost:3306/javatechie
spring.datasource.username = root
spring.datasource.password = ENC(llo8Tfwc2cBLNAzjkksTk9dBj8tIwT3ZUHDQoFQm88D85qJTTY9doPcmQiN/Emtd)
Now to run the Spring Boot application in any IDE, you need to pass VM argument which we already discussed above -Djasypt.encryptor.password=javatechie
6. Encrypt credential in application.yml file
By default, Jasypt will update the application.properties file. In case you’re using application.yml in your project,
First, wrap the values password inside DEC() as shown below:
spring:
  datasource:
    username: root
    password: DEC(Password)
    url: jdbc:mysql://localhost:3306/javatechie
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
    properties.hibernate.dialect: org.hibernate.dialect.MySQL5Dialect
then run the below specify the path of the file(application.yml) in the command like this:
mvn jasypt:encrypt -Djasypt.encryptor.password=javatechie -Djasypt.plugin.path="file:src/main/resources/application.yml"
Then it will replace the DEC() placeholders in the application.yml file with the encrypted value:
spring:
  datasource:
    username: root
    password: ENC(y3K0rZ78+/nFxd3DXnUBw2qQ4kuEwOsg3rE3iZ9foTtH9tnTMIMzVKVHlLL5fex5)
    url: jdbc:mysql://localhost:3306/javatechie
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
    properties.hibernate.dialect: org.hibernate.dialect.MySQL5Dialect
Now to run the Spring Boot application by passing VM argument .
7. Configure encryptor in Spring configuration class
Jasypt is easy to use, as you’ve seen with the commands above. And it is also highly configurable if you have some knowledge in cryptography and you want to customize settings for the encryptor. For example, create a new Spring configuration class in the project as follows:
@Configuration
public class JasyptEncryptorConfig {
    @Bean(name = "jasyptStringEncryptor")
    public StringEncryptor getPasswordEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("javatechie"); // encryptor's private key
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }
}
This code will override the default encryption configuration, so you need to write some code to encrypt a password I would recommend to write test case to generate encrypted key
@Test
public void testPasswordEncryption() {
    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
    config.setPassword("javatechie"); // encryptor's private key
    config.setAlgorithm("PBEWithMD5AndDES");
    config.setKeyObtentionIterations("1000");
    config.setPoolSize("1");
    config.setProviderName("SunJCE");
    config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
    config.setStringOutputType("base64");
    encryptor.setConfig(config);
    String plainText = "Password";
    String encryptedPassword = encryptor.encrypt(plainText);
    System.out.println("encryptedPassword : " + encryptedPassword);
}
Run the test case

Next , copy this encrypted key and add it in your application config file with prefix ENC like below
spring:
  datasource:
    username: root
    password: ENC(khgTcS1GHuwhynCBlKTLU2G7xtJsSvcD)
    url: jdbc:mysql://localhost:3306/javatechie
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
    properties.hibernate.dialect: org.hibernate.dialect.MySQL5Dialect
That’s the tutorial about encrypting passwords in Spring Boot configuration file using Jasypt library
YouTube link : https://youtu.be/Ll-Cz6h8s6I
