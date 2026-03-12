import devConfig from "./dev";
import qaConfig from "./qa";
import prodConfig from "./prod";

const env = process.env.NEXT_PUBLIC_ENV;

let config;

if (env === "qa") {
  config = qaConfig;
} else if (env === "production") {
  config = prodConfig;
} else {
  config = devConfig;
}

export default config;