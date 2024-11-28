#!/bin/bash

# Define the list of services and their Dockerfile locations
declare -A services
services=(
  ["product-service"]="Product-Service/Dockerfile.product"
  ["order-service"]="Order-Service/Dockerfile.order"
  ["gateway-service"]="gateway-service/Dockerfile.gateway"
  ["eureka-server"]="EurekaServer/Dockerfile.eureka"
  ["customer-service"]="Customer-Service/Dockerfile.customer"
  ["config-server"]="configurationServer/Dockerfile.config"
  ["admin-server"]="admin-server/Dockerfile.admin"
)

# DockerHub username (replace with your own)
DOCKER_USERNAME="figuiguihassan"

# Loop through the services and build, then push each Docker image
for service in "${!services[@]}"; do
  # Get the Dockerfile path
  dockerfile="${services[$service]}"

  # Build the image
  echo "Building $service image..."
  docker build -t "$DOCKER_USERNAME/$service:latest" -f "$dockerfile" .

  # Push the image to Docker Hub
  echo "Pushing $service image to Docker Hub..."
  docker push "$DOCKER_USERNAME/$service:latest"

  echo "$service image built and pushed successfully."
done

echo "All services have been built and pushed."



# TO RUN THIS SCRIPT : chmod +x build_and_push.sh           ./build_and_push.sh