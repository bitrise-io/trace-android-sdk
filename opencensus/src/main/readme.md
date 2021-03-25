# Open Census Proto Files

We have copied the [proto files from opencensus](https://github.com/census-instrumentation/opencensus-proto/tree/master/src/opencensus/proto) as we experienced issues with their provided library.

We can easily compile these proto files ourselves, so this gives us more control over the process.

## Updating Files

1. You need to download a proto compiler e.g. https://formulae.brew.sh/formula/protobuf
2. Using a terminal browser navigate to this folder this readme file is located
3. Run `protoc --java_out=java main/proto/**/*.proto`
