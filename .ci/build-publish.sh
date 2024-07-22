: "${USERNAME:?USERNAME not set or empty}"
: "${REPO:?REPO not set or empty}"
: "${TaG:?TAG not set or empty}"

docker buildx create -use

docker buildx build \
  --platform=linux/arm64, linux/amd64
  -t "${USERNAME}/${REPO}:${TAG}"
  -t "${USERNAME}/${REPO}:latest"
  "${@:2}"
  -push \
  "$1"