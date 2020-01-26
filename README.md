#SakuraNMSTranslator
扔掉反射写NMS代码吧 / Write NMS code without Reflection

## 使用 ##
例程/Sample: [sample-actionbar-api](https://github.com/SakuraKoi/SakuraNMSTranslator/tree/master/sample-actionbar-api)

创建ImplLoader实例
> ImplLoader implLoader = ImplLoader.forPlugin(this, "sakura.kooi.ActionbarAPI.impl", true)

创建接口: [Link](https://github.com/SakuraKoi/SakuraNMSTranslator/blob/master/sample-actionbar-api/src/sakura/kooi/ActionbarAPI/ActionBar.java)

创建对应版本实现: [MC_1.8-1.11](https://github.com/SakuraKoi/SakuraNMSTranslator/blob/master/sample-actionbar-api/src/sakura/kooi/ActionbarAPI/impl/ActionBar/NMS_v8_v11.java) [MC_1.12+](https://github.com/SakuraKoi/SakuraNMSTranslator/blob/master/sample-actionbar-api/src/sakura/kooi/ActionbarAPI/impl/ActionBar/NMS_v12_.java)

实现类使用`@SupportedVersion`注解: `lowestVersion` (所支持的最低版本)和`highestVersion`(所支持的最高版本), `implVersion`是说明文字

加载实现类
> private static ActionBar impl;
> impl = implLoader.of(ActionBar.class, ActionbarReflection.class);

后面的参数是无匹配版本时的fallback

使用
> impl.sendActionBar(player, message);