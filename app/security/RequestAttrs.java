package security;

import play.libs.typedmap.TypedKey;

public final class RequestAttrs {
    private RequestAttrs() {
    }

    public static final TypedKey<Long> USER_ID = TypedKey.create("userId");
}
