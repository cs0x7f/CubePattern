
package cs.cubepattern;
import java.util.Arrays;
import java.util.Comparator;
import java.io.*;
import static cs.cubepattern.FaceletCube.*;

import java.util.Random;

public class CubePattern {

    static boolean isInited = false;

    static int[][] color_map = new int[][] {
        {U, R, F, D, L, B},
        {U, F, L, D, B, R},
        {U, L, B, D, R, F},
        {U, B, R, D, F, L}
    };

    static String[] move2str = new String[] {
        "U ", "U2", "U'", "R ", "R2", "R'", "F ", "F2", "F'",
        "D ", "D2", "D'", "L ", "L2", "L'", "B ", "B2", "B'"
    };

    static int pattern_count = 0;
    static int[][] unique_set = new int[53683][];//unique_set[0]: val, unique_set[1~end]: moves
    static int[] pattern_bitmap = new int[1 << 24 >> 5];

    static int[] moves = new int[10];

    static int get_bit(int[] bitmap, int idx) {
        return pattern_bitmap[idx >> 5] >> (idx & 0x1f) & 1;
    }

    static void set_bit(int[] bitmap, int idx) {
        pattern_bitmap[idx >> 5] |= 1 << (idx & 0x1f);
    }

    static int fill_pattern_set(int val, int faceIdx) {
        unique_set[unique_cnt] = new int[global_depth + 1];
        unique_set[unique_cnt][0] = val;
        for (int i = 0; i < global_depth; i++) {
            int axis = moves[i] / 3;
            int power = moves[i] % 3;
            axis = face_map[faceIdx][axis];
            unique_set[unique_cnt][i + 1] = axis * 3 + power;
        }
        return 0;
    }

    static int global_depth = 0;
    static int unique_cnt = 0;

    static int proc_face_hash(int face, int faceIdx) {
        int idx = face4to3(face);
        if (get_bit(pattern_bitmap, idx) != 0) {
            return 0;
        }
        set_bit(pattern_bitmap, idx);
        fill_pattern_set(idx, faceIdx);
        pattern_count++;
        unique_cnt++;
        // System.out.print(String.format("\t\t\t%d\r", unique_cnt));

        int val = 0;
        for (int mirIdx = 0; mirIdx < 2; ++mirIdx) {
            for (int colIdx = 0; colIdx < 4; ++colIdx) {
                val = 0;
                int[] cur_col_map = color_map[colIdx];

                for (int i = 0; i < 8; ++i) {
                    val |= cur_col_map[(face >> i * 4) & 0x7] << i * 3;
                }
                for (int rotate = 0; rotate < 24; rotate += 6) {
                    idx = val >> rotate | val << 24 - rotate & 0x00ffffff;
                    if (get_bit(pattern_bitmap, idx) == 0) {
                        set_bit(pattern_bitmap, idx);
                        pattern_count++;
                    }
                }
            }
            face = faceMirror(face);
        }
        return 1;
    }

    static int proc_pattern(FaceletCube fc) {
        int has_new = 0;
        has_new += proc_face_hash(faceMap(fc.face_U, 0), 0);
        has_new += proc_face_hash(faceMap(fc.face_R, 1), 1);
        has_new += proc_face_hash(faceMap(fc.face_F, 2), 2);
        has_new += proc_face_hash(faceMap(fc.face_D, 3), 3);
        has_new += proc_face_hash(faceMap(fc.face_L, 4), 4);
        has_new += proc_face_hash(faceMap(fc.face_B, 5), 5);
        return has_new;
    }

    static boolean checkMoves() {
        if (moves[0] == Ux1) {
            if (moves[1] == Rx1 || moves[1] == Rx2 || moves[1] == Rx3) {
                return true;
            } else if (moves[1] == Dx1 || moves[1] == Dx2 || moves[1] == Dx3) {
                return moves[2] == Rx1 || moves[2] == Rx2 || moves[2] == Rx3;
            } else {
                return false;
            }
        } else if (moves[0] == Ux2) {
            if (moves[1] == Rx1 || moves[1] == Rx2) {
                return true;
            } else if (moves[1] == Dx2) {
                return moves[2] == Rx1 || moves[2] == Rx2;
            }
        }
        return false;
    }

    static boolean search(int maxl, int depth, int last_axis, FaceletCube fc) {
        if (depth == 3 && !checkMoves()) {
            return false;
        }
        if (maxl <= 0) {
            proc_pattern(fc);
            return pattern_count == 1679616;
        }
        for (int axis = 0; axis < 18; axis += 3) {
            if (axis == last_axis || axis == last_axis - 9) {
                continue;
            }
            for (int power = 0; power < 4; power++) {
                fc.doMove(axis);
                if (power == 3) {
                    break;
                }
                moves[depth] = axis + power;
                if (search(maxl - 1, depth + 1, axis, fc)) {
                    return true;
                }
            }
        }
        return false;
    }

    static int[] binarySearch(int val) {
        int result = Arrays.binarySearch(unique_set, new int[] {val}, new Comparator<int[]>() {
            public int compare(int[] arr1, int[] arr2) {
                return arr1[0] - arr2[0];
            }
        });
        if (result >= 0) {
            return unique_set[result];
        }
        return null;
    }

    static int[][] mirror_map = new int[][] {
        {Ux1, Ux2, Ux3, Rx1, Rx2, Rx3, Fx1, Fx2, Fx3, Dx1, Dx2, Dx3, Lx1, Lx2, Lx3, Bx1, Bx2, Bx3},
        {Ux3, Ux2, Ux1, Lx3, Lx2, Lx1, Fx3, Fx2, Fx1, Dx3, Dx2, Dx1, Rx3, Rx2, Rx1, Bx3, Bx2, Bx1}
    };

    static int[] rotate_map = new int[] {
        Ux1, Ux2, Ux3, Bx1, Bx2, Bx3, Rx1, Rx2, Rx3, Dx1, Dx2, Dx3, Fx1, Fx2, Fx3, Lx1, Lx2, Lx3
    };

    static String[] face_rot = new String[] {
        "", "z' y' ", "x  y  ", "z2 y  ", "z  y2 ", "x' y2 "
    };

    static String[] face_rot_pre = new String[] {
        "", "z' ", "x  ", "z2 ", "z  ", "x' "
    };

    static int[] face_rot_y = new int[] {
        0, 3, 1, 1, 2, 2
    };

    static String getResult(int uniIdx, int faceIdx, int mirIdx, int colIdx, int rotate) {
        StringBuffer sb = new StringBuffer();
        // System.out.println(String.format("%3d%3d%3d%3d", faceIdx, mirIdx, colIdx, rotate));
        int[] result_ref = binarySearch(uniIdx);
        int[] result = new int[result_ref.length - 1];

        for (int i = 0; i < result.length; i++) {
            result[i] = result_ref[i + 1];
        }

        rotate = (rotate / 6 + colIdx) & 3;
        while (colIdx > 0) {
            colIdx--;
            for (int i = 0; i < result.length; i++) {
                result[i] = rotate_map[result[i]];
            }
        }


        if (mirIdx != 0) {
            for (int i = 0; i < result.length; i++) {
                result[i] = mirror_map[mirIdx][result[i]];
            }
            rotate = (4 - rotate) % 4;
        }

        for (int r = 0; r < (40 - rotate) % 4; r++) {
            for (int i = 0; i < result.length; i++) {
                result[i] = rotate_map[result[i]];
            }
        }
        sb.append(face_rot_pre[faceIdx]);
        sb.append(new String[] {"", "y  ", "y2 ", "y' "} [(rotate + face_rot_y[faceIdx]) % 4]);
        for (int i = 0; i < result.length; i++) {
            sb.append(move2str[result[i]] + " ");
        }
        return sb.toString();
    }

    /**
     *  012
     *  345 -> "012345678"
     *  678
     */
    public static String findPattern(String input) {
        if (!isInited) {
            init();
        }
        int faceIdx = "URFDLB".indexOf(input.charAt(4));
        int val = 0;
        int[] cur_face_map = face_map[faceIdx];
        val |= cur_face_map["URFDLB".indexOf(input.charAt(0))] << 0 * 4;
        val |= cur_face_map["URFDLB".indexOf(input.charAt(1))] << 1 * 4;
        val |= cur_face_map["URFDLB".indexOf(input.charAt(2))] << 2 * 4;
        val |= cur_face_map["URFDLB".indexOf(input.charAt(5))] << 3 * 4;
        val |= cur_face_map["URFDLB".indexOf(input.charAt(8))] << 4 * 4;
        val |= cur_face_map["URFDLB".indexOf(input.charAt(7))] << 5 * 4;
        val |= cur_face_map["URFDLB".indexOf(input.charAt(6))] << 6 * 4;
        val |= cur_face_map["URFDLB".indexOf(input.charAt(3))] << 7 * 4;
        int face = faceMap(val, 0);

        val = 0;
        for (int mirIdx = 0; mirIdx < 2; ++mirIdx) {
            for (int colIdx = 0; colIdx < 4; ++colIdx) {
                val = 0;
                for (int i = 0; i < 8; ++i) {
                    val |= color_map[colIdx][(face >> i * 4) & 0x7] << i * 3;
                }
                for (int rotate = 0; rotate < 24; rotate += 6) {
                    int idx = val >> rotate | val << 24 - rotate & 0x00ffffff;
                    if (binarySearch(idx) != null) {
                        return getResult(idx, faceIdx, mirIdx, colIdx, rotate);
                    }
                }
            }
            face = faceMirror(face);
        }
        return null;
    }

    static synchronized void init() {
        if (isInited) {
            return;
        }
        isInited = true;
        FaceletCube fc = new FaceletCube();
        for (int depth = 0; depth < 10; ++depth) {
            global_depth = depth;
            search(depth, 0, -1, fc);
            // System.out.print(String.format("%d\t%d\n", depth, pattern_count));
        }
        Arrays.sort(unique_set, new Comparator<int[]>() {
            public int compare(int[] arr1, int[] arr2) {
                return arr1[0] - arr2[0];
            }
        });
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // init();

        if (args.length == 0) {
            Random r = new Random();
            for (int n = 0; n < 20; n++) {
                char[] rnd = new char[9];
                for (int i = 0; i < 9; i++) {
                    rnd[i] = "URFDLB".charAt(r.nextInt(6));
                }
                String input = new String(rnd);
                System.out.println("" + rnd[0] + rnd[1] + rnd[2]);
                System.out.println("" + rnd[3] + rnd[4] + rnd[5]);
                System.out.println("" + rnd[6] + rnd[7] + rnd[8]);
                String result = findPattern(input);
                System.out.println(result);
                System.out.println();
            }
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
            int row = 0;
            int tot_length = 0;
            int[] length_dis = new int[13];
            String[] buf = new String[3];
            while (true) {
                String data = in.readLine();
                if (data == null) {
                    if (row != 0) {
                        System.out.println("Incorrect Input");
                    }
                    break;
                }
                System.out.println(data);
                buf[row++] = data;
                if (row == 3) {
                    if (buf[0].length() != buf[1].length() || buf[1].length() != buf[2].length() || buf[0].length() % 3 != 0) {
                        System.out.println("Incorrect Input");
                        return;
                    }
                    int length = buf[0].length() / 3;
                    for (int i = 0; i < length; i++) {
                        String input = new String(
                            new char[] {
                                buf[0].charAt(i * 3 + 0), buf[0].charAt(i * 3 + 1), buf[0].charAt(i * 3 + 2),
                                buf[1].charAt(i * 3 + 0), buf[1].charAt(i * 3 + 1), buf[1].charAt(i * 3 + 2),
                                buf[2].charAt(i * 3 + 0), buf[2].charAt(i * 3 + 1), buf[2].charAt(i * 3 + 2)
                            });
                        String result = findPattern(input);
                        System.out.println("" + buf[0].charAt(i * 3 + 0) + buf[0].charAt(i * 3 + 1) + buf[0].charAt(i * 3 + 2));
                        System.out.println("" + buf[1].charAt(i * 3 + 0) + buf[1].charAt(i * 3 + 1) + buf[1].charAt(i * 3 + 2) + ": " + result);
                        System.out.println("" + buf[2].charAt(i * 3 + 0) + buf[2].charAt(i * 3 + 1) + buf[2].charAt(i * 3 + 2));
                        // System.out.println(result);
                        System.out.println();

                        int cur_len = result.length() / 3;
                        if (cur_len > 0 && "xyz".indexOf(result.charAt(0)) != -1) {
                            cur_len--;
                        }
                        if (cur_len > 0 && "xyz".indexOf(result.charAt(3)) != -1) {
                            cur_len--;
                        }
                        tot_length += cur_len;
                        length_dis[cur_len]++;
                    }
                    row = 0;
                }
            }
            System.out.println(tot_length);
            for (int i = 0; i < 13; i++) {
                System.out.println(i + ": " + length_dis[i]);
            }
        }
    }
}