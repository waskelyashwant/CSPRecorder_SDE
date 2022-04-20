import scipy.io.wavfile as wavfile
import scipy.fftpack as fftpk
# from matplotlib import pyplot as plt
import math


def main(fileName):
	print("fileName - python ", fileName, "hello")
	s_rate, signal = wavfile.read(fileName)
	print(s_rate, signal)

	FFT = abs(fftpk.fft(signal))
	freqs = fftpk.fftfreq(len(FFT), (1.0/s_rate))
	res_FFT_aud=[]
	res_freq=[]
	FFT_all=[]
	for i in range(0,len(freqs)//2):
	    x = FFT[i]*1e-5
	    s=x*x
	    FFT_all.append(s)
	    if freqs[i]>=20 and freqs[i]<=20000:
	        res_FFT_aud.append(s)

	print("FFT_all", len(FFT_all))
	print("res_FFT_aud", len(res_FFT_aud))
	       
	FFT_all.sort(reverse = True)
	top_20_all=FFT_all[0:20]
	top_50_all=FFT_all[0:50]
	ene_20_all = sum(top_20_all)/len(top_20_all)
	ene_50_all = sum(top_50_all)/len(top_50_all)
	ene_all = sum(FFT_all)/len(FFT_all)
	ene_20_all_db = 10*math.log10(ene_20_all)
	ene_50_all_db = 10*math.log10(ene_50_all)
	ene_all_db = 10*math.log10(ene_all)
	


	res_FFT_aud.sort(reverse = True)
	top_20_aud=res_FFT_aud[0:20]
	top_50_aud=res_FFT_aud[0:50]
	# print(sum(top_20))
	ene_20_aud = sum(top_20_aud)/len(top_20_aud)
	ene_50_aud = sum(top_50_aud)/len(top_50_aud)
	ene_aud_all = sum(res_FFT_aud)/len(res_FFT_aud)
	ene_20_aud_db = 10*math.log10(ene_20_aud)
	ene_50_aud_db = 10*math.log10(ene_50_aud)
	ene_aud_all_db = 10*math.log10(ene_aud_all)

	print(ene_20_all)
	print(ene_20_all_db)
	print(ene_50_all)
	print(ene_50_all_db)

	print(ene_20_aud)
	print(ene_20_aud_db)
	print(ene_50_aud)
	print(ene_50_aud_db)
	print(ene_all)
	print(ene_all_db)
	print(ene_aud_all)
	print(ene_aud_all_db)
	
	return [float(ene_20_all), float(ene_20_all_db), float(ene_50_all), float(ene_50_all_db), float(ene_20_aud), float(ene_20_aud_db),
	        float(ene_50_aud), float(ene_50_aud_db), float(ene_all), float(ene_all_db), float(ene_aud_all), float(ene_aud_all_db)]

