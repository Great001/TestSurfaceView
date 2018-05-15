# 使用SurfaceView实现进度条刷新
## SurfaceView默认是黑色背景，在Activity所在窗口的下面显示。此时，如果SurfaceView设置了背景的话，背景会在Surface的上面。
## 一般需要在SurfaceView的构造方法中
>  //设置了这个之后，即使设置了背景，也不可见，如果没设的话，背景会显示在Surface的上面
>
>         setZOrderOnTop(true);
>         getHolder().setFormat(PixelFormat.TRANSLUCENT);
## 根据文档，此时SurfaceView会显示在Activity窗口上面，之前设置的背景也会消失