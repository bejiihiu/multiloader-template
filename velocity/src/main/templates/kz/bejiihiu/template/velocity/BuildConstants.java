package kz.bejiihiu.template.velocity;

// Константы подставляются до компиляции таской generateTemplates,
// чтобы аннотация @Plugin видела версию без хардкода.
public class BuildConstants {
    public static final String VERSION = "${version}";
}
