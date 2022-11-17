// vue.config.js
module.exports = {
  css: {
    loaderOptions: {
      sass: {
        prependData: `
        @import "@/assets/styles/variables.scss"; 
      `,
      },
    },
  },
  configureWebpack: {
    resolve: {
      fallback: { crypto: false, util: false },
    },
    experiments: {
      // asyncWebAssembly: true,
      // buildHttp: true,
      // layers: true,
      // lazyCompilation: true,
      // outputModule: true,
      // syncWebAssembly: true,
      topLevelAwait: true,
    },
  },
};
