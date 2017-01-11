/*
    012
    7U3
    654
670 234 456 012
5L1 1F5 3R7 7B3
412 076 210 654
    670
    5D1
    412
*/

package cs.cubepattern;

class FaceletCube {

    static final int Ux1 =  0;
    static final int Ux2 =  1;
    static final int Ux3 =  2;
    static final int Rx1 =  3;
    static final int Rx2 =  4;
    static final int Rx3 =  5;
    static final int Fx1 =  6;
    static final int Fx2 =  7;
    static final int Fx3 =  8;
    static final int Dx1 =  9;
    static final int Dx2 = 10;
    static final int Dx3 = 11;
    static final int Lx1 = 12;
    static final int Lx2 = 13;
    static final int Lx3 = 14;
    static final int Bx1 = 15;
    static final int Bx2 = 16;
    static final int Bx3 = 17;

    static final int U = 0;
    static final int R = 1;
    static final int F = 2;
    static final int D = 3;
    static final int L = 4;
    static final int B = 5;

    int face_U = 0x00000000;
    int face_R = 0x11111111;
    int face_F = 0x22222222;
    int face_D = 0x33333333;
    int face_L = 0x44444444;
    int face_B = 0x55555555;

    void doMove(int move) {

        int face_U_new = 0;
        int face_R_new = 0;
        int face_F_new = 0;
        int face_D_new = 0;
        int face_L_new = 0;
        int face_B_new = 0;

        switch (move) {
        case  0:    //U
            face_U_new = face_U >> 24 | face_U << 8;
            face_D_new = face_D;
            face_B_new = face_B & 0xfffff000 | (face_L <<  8 | face_L >> 24) & ~0xfffff000;
            face_L_new = face_L & 0x00fffff0 | (face_F >> 16 | face_F << 16) & ~0x00fffff0;
            face_F_new = face_F & 0xfff000ff |  face_R >>  8 & ~0xfff000ff;
            face_R_new = face_R & 0xf000ffff |  face_B << 16 & ~0xf000ffff;
            break;
        case  3:    //R
            face_R_new = face_R >> 24 | face_R << 8;
            face_L_new = face_L;
            face_D_new = face_D & 0xfffff000 | (face_B <<  8 | face_B >> 24) & ~0xfffff000;
            face_B_new = face_B & 0x00fffff0 | (face_U >> 16 | face_U << 16) & ~0x00fffff0;
            face_U_new = face_U & 0xfff000ff |  face_F >>  8 & ~0xfff000ff;
            face_F_new = face_F & 0xf000ffff |  face_D << 16 & ~0xf000ffff;
            break;
        case  6:    //F
            face_F_new = face_F >> 24 | face_F << 8;
            face_B_new = face_B;
            face_L_new = face_L & 0xfffff000 | (face_D <<  8 | face_D >> 24) & ~0xfffff000;
            face_D_new = face_D & 0x00fffff0 | (face_R >> 16 | face_R << 16) & ~0x00fffff0;
            face_R_new = face_R & 0xfff000ff |  face_U >>  8 & ~0xfff000ff;
            face_U_new = face_U & 0xf000ffff |  face_L << 16 & ~0xf000ffff;
            break;
        case  9:    //D
            face_D_new = face_D >> 24 | face_D << 8;
            face_U_new = face_U;
            face_R_new = face_R & 0xfffff000 | (face_F <<  8 | face_F >> 24) & ~0xfffff000;
            face_F_new = face_F & 0x00fffff0 | (face_L >> 16 | face_L << 16) & ~0x00fffff0;
            face_L_new = face_L & 0xfff000ff |  face_B >>  8 & ~0xfff000ff;
            face_B_new = face_B & 0xf000ffff |  face_R << 16 & ~0xf000ffff;
            break;
        case 12:    //L
            face_L_new = face_L >> 24 | face_L << 8;
            face_R_new = face_R;
            face_F_new = face_F & 0xfffff000 | (face_U <<  8 | face_U >> 24) & ~0xfffff000;
            face_U_new = face_U & 0x00fffff0 | (face_B >> 16 | face_B << 16) & ~0x00fffff0;
            face_B_new = face_B & 0xfff000ff |  face_D >>  8 & ~0xfff000ff;
            face_D_new = face_D & 0xf000ffff |  face_F << 16 & ~0xf000ffff;
            break;
        case 15:    //B
            face_B_new = face_B >> 24 | face_B << 8;
            face_F_new = face_F;
            face_U_new = face_U & 0xfffff000 | (face_R <<  8 | face_R >> 24) & ~0xfffff000;
            face_R_new = face_R & 0x00fffff0 | (face_D >> 16 | face_D << 16) & ~0x00fffff0;
            face_D_new = face_D & 0xfff000ff |  face_L >>  8 & ~0xfff000ff;
            face_L_new = face_L & 0xf000ffff |  face_U << 16 & ~0xf000ffff;
            break;
        }

        face_U = face_U_new;
        face_R = face_R_new;
        face_F = face_F_new;
        face_D = face_D_new;
        face_L = face_L_new;
        face_B = face_B_new;
    }

    static int faceRotate(int val, int rotate) {
        rotate *= 8;
        return val >> (32 - rotate) | val << rotate;
    }

    static int face4to3(int val) {
        int ret = 0;
        for (int i = 0; i < 8; i++) {
            ret |= (val >> i * 4 & 0xf) << i * 3;
        }
        return ret;
    }

    static int[] colorMirror = new int[] {U, L, F, D, R, B};
    static int[] mirrorIdx = new int[] {2, 1, 0, 7, 6, 5, 4, 3};
    // // 76543210
    // //            ----321-                 765-----                 ---4---0
    // val = val & 0x0000fff0 << 16 | val & 0xfff00000 >> 16 | val & 0x000f000f;
    // // 32147650 = 321-----       |         ----765-       |         ---4---0
    // //            --1---5-                 3---7---                 -2-4-6-0
    // val = val & 0x00f000f0 <<  8 | val & 0xf000f000 >> 8  | val & 0x0f0f0f0f;
    // // 12345670 = 1---5---       |         --3---7-       |         -2-4-6-0
    static int faceMirror(int val) {
        // 76543210
        // 34567012
        int ret = 0;
        for (int i = 0; i < 8; i++) {
            ret |= colorMirror[val >> mirrorIdx[i] * 4 & 0xf] << i * 4;
        }
        return ret;
    }

    static int[][] face_map = new int[][] {
        {U, R, F, D, L, B},
        {F, U, R, B, D, L},
        {R, F, U, L, B, D},
        {D, B, L, U, F, R},
        {L, D, B, R, U, F},
        {B, L, D, F, R, U}
    };

    static int faceMap(int val, int faceIdx) {
        int ret = 0;
        for (int i = 0; i < 8; i++) {
            ret |= face_map[faceIdx][val >> i * 4 & 0xf] << i * 4;
        }
        return ret;
    }
}