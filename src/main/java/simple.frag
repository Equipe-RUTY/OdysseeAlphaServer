// Blinn 1982
// https://www.microsoft.com/en-us/research/wp-content/uploads/1982/07/p235-blinn.pdf
#version 110
struct Blob {
    vec2 c;
    float r; // radius
    float b; // blobinness
    vec3 color;
};

// modifier ici si besoin
uniform Blob blobs[100];
uniform int blobCount;

void main()
{        
    gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
    vec3 color = vec3(0.0, 0.0, 0.0);
    float val = 0.0;
    for (int i = 0; i< blobCount; i++) {
    	float d = dot(gl_FragCoord.xy - blobs[i].c, gl_FragCoord.xy - blobs[i].c);
        float blob_val = exp(blobs[i].b * d * d / (blobs[i].r * blobs[i].r) - blobs[i].b);
        color += blob_val * blobs[i].color;
        val += blob_val;
    }
    if (val >= 1.0) {
        gl_FragColor = vec4(color.rgb/val, 1.0);
    } else {
    	gl_FragColor = vec4(color.rgb * val * val * val * val, 1.0);
    }
}
