FROM eclipse-temurin:21-jdk

# Copy app JARs into container
COPY jars/ /apps/

# Copy all shell scripts from /shell into /apps/
COPY shell/*.sh /apps/

# Make scripts executable
RUN chmod +x /apps/*.sh

# Install MySql to run from Bash
#RUN apt-get update && apt-get install -y mysql-client

# Optional: Launch orchestration script automatically
# CMD ["/apps/start-env.sh"]