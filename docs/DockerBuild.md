## Build and publish Dockers for x86_64 and ARM64
```
export VERSION=1.2.3
# on x86 AMD64 device:
docker build -t jurajveverka/file-server:${VERSION}-amd64 --build-arg ARCH=amd64 --file ./Dockerfile . 
docker push jurajveverka/file-server:${VERSION}-amd64

# on ARM64 v8 device:
docker build -t jurajveverka/file-server:${VERSION}-arm64v8 --build-arg ARCH=arm64v8 --file ./Dockerfile .
docker push jurajveverka/file-server:${VERSION}-arm64v8

# on x86 AMD64 device: 
docker manifest create \
jurajveverka/file-server:${VERSION} \
--amend jurajveverka/file-server:${VERSION}-amd64 \
--amend jurajveverka/file-server:${VERSION}-arm64v8

docker manifest push jurajveverka/file-server:${VERSION}
```
