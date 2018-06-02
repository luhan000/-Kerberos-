package DES;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;


public class BitArray implements Cloneable{
	
	private byte[] byteArray;
	private int size;
	
	public BitArray() {
		this(8, 0);
	}
	
	
	public BitArray(int size) {
		this(size, 0);
	}
	
	public BitArray(byte[] b) {
		byteArray = Arrays.copyOf(b, b.length);
		size = b.length * 8;
	}
	
	public BitArray(byte[] b, int len) {
		size = b.length * len;
		byteArray = new byte[(int) Math.ceil((double)size/8)];
		
	}
	
	public BitArray(int size, int value) {
		int byteSize = (int) Math.ceil((double)size / 8);  //ע�⣬������ת��Ϊdouble�ܹؼ�
		byteArray = new byte[byteSize];
		this.size = size;
		
		if(value == 1)
			for(int i=0; i<size; i++)
				set(i);
	}
	
	// ��ָ��λ����Ϊ 1
	public void set(int position) {
		if (position < 0 || position >= size) return;
		int bitOffset = 7 - position % 8;
		int byteOffset = position / 8;
		
		byteArray[byteOffset] |= (byte)(1 << bitOffset);
	}
	
	// ��ָ��λ����Ϊ0
	public void clear(int position) {
		if (position < 0 || position >= size) return;
		int bitOffset = 7 - position % 8;
		int byteOffset = position / 8;
		
		byteArray[byteOffset] &= (byte)(1 << bitOffset);
	}
	
	// ��ȡָ��λ��ֵ�� trueΪ 1
	public boolean get(int position) {
		if (position < 0 || position >= size) return false;
		int bitOffset = 7 - position % 8;
		int byteOffset = position / 8;
		
		return (byteArray[byteOffset] & (1 << bitOffset)) != 0;
	}
	
	public BitArray xor(BitArray b) {
		if(b.size != size) return null;
		BitArray result = new BitArray(size);
		for(int i=0; i<byteArray.length; i++)
			result.byteArray[i] = (byte) (byteArray[i] ^ b.byteArray[i]);
		return result;
	}
	
	// �������Ϊ����������Ȳ���
	public List<BitArray> split() {
		int subSize = size / 2;
		BitArray left = new BitArray(subSize);
		BitArray right = new BitArray(subSize);
		
		for(int i=0; i<subSize; i++) {
			if(get(i)) left.set(i);
		}
		for(int i=subSize; i<size; i++) {
			if(get(i)) right.set(i-subSize);
		}
		List<BitArray> result = new ArrayList<>();
		result.add(left);
		result.add(right);
		return result;
	}
	
	// �ϲ�����λͼ
	public void union(BitArray left, BitArray right) {
		size = left.size + right.size;
		byteArray = new byte[(int) Math.ceil((double)size/8)];
		for(int i=0; i<left.size; i++) {
			if(left.get(i)) set(i);
		}
		for(int i=0; i<right.size;i++) {
			if(right.get(i)) set(i+left.size);
		}
	}
	
	// ��λ����ѭ������stepλ
	public void move(int step) {
		boolean[] tmp = new boolean[step];
		for(int i=0; i<step; i++) {
			tmp[i] = get(i);
		}
		for(int i=step; i<size; i++) {
			if(get(i)) set(i-step);
			else clear(i-step);
		}
		for(int i=0; i<step; i++) {
			if(tmp[i]) set(size-step+i);
			else clear(size-step+i);
		}
	}
	
	// ��λ�����֣�ÿ��λ�ŵ�һ����Ԫ�����ڴ���/ѡ��
	public BitArray[] splitForSelect() {
		if(size != 48) System.out.println("λ������ȷ��Ӧ����48λ");
		BitArray[] result = new BitArray[8];
		for(int i=0; i<size; i+=6) {
			BitArray b = new BitArray(6);
			for(int j=0; j<6; j++){
				if(get(i+j)) b.set(j);
			}
			result[i/6] = b;
		}
		return result;
	}
	
	// ������int����ÿһλת��Ϊbyte,��ȡǰ��λ�ϲ������ڴ���/ѡ��
	public void unionForSelect(int[] in) {
		for(int i=0; i<in.length; i++) {
			byte tmp = new Integer(in[i]).byteValue();
			for(int j=0; j<4; j++) {
				boolean r = (tmp & (1 << j)) != 0;
				if(r) set(i*4 + j);
			}
		}
	}
	
	// ��λ������ʾΪλͼ��ʽ
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
 		for (int i=0; i<size; i++) {
			if(get(i)) s.append("1");
			else s.append("0");
			if((i+1) % 8 == 0) s.append("\n");
		}
		return s.toString();
	}
	
	// ��ʾλ��������String
	public String getString() throws UnsupportedEncodingException {
		return Base64.getEncoder().encodeToString(byteArray);
		// return new String(byteArray, "UTF-8");
	}
	
	@Override
	public Object clone() {
;		
		BitArray bitArray = null;
		try {
			bitArray = (BitArray) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		bitArray.byteArray = Arrays.copyOf(this.byteArray, byteArray.length);
		bitArray.size = this.size;
		return bitArray;
	}
	
	public byte[] getBytes() {
		return byteArray;
	}
	
}

