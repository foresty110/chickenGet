'use client';

import { useState } from 'react';
import { drawGacha } from '@/services/api';
import { GachaStatus } from '@/types';

export default function GachaPage() {
  const [status, setStatus] = useState<GachaStatus>('IDLE');
  const [resultMsg, setResultMsg] = useState('');

  const handleDraw = async () => {
    console.log('--- 가챠 클릭! ---');
    setStatus('LOADING');
    
    try {
      const response = await drawGacha(1); 
      console.log('서버 결과:', response);
      
      if (response.success) {
        setStatus('SUCCESS');
        setResultMsg(response.data || '축하합니다!');
      } else {
        setStatus('ERROR');
        setResultMsg(response.message || '가챠 실패');
      }
    } catch (error) {
      console.error('API 에러:', error);
      setStatus('ERROR');
      setResultMsg('서버와 통신하는 중 오류가 발생했습니다.');
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50 p-6">
      <div className="bg-white p-10 rounded-3xl shadow-xl w-full max-w-md text-center border border-gray-100">
        <h1 className="text-4xl font-extrabold text-orange-600 mb-8">🍗 치킨 가챠</h1>
        
        {status === 'IDLE' && (
          <button
            onClick={handleDraw}
            className="group relative w-full py-5 bg-orange-500 hover:bg-orange-600 text-white font-black text-xl rounded-2xl transition-all active:scale-95 shadow-lg overflow-hidden"
          >
            <span className="relative z-10">쿠폰 뽑기 도전!</span>
            <div className="absolute inset-0 bg-white opacity-0 group-active:opacity-10 transition-opacity"></div>
          </button>
        )}

        {status === 'LOADING' && (
          <div className="flex flex-col items-center space-y-6 py-4">
            <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-orange-500"></div>
            <p className="text-lg font-bold text-gray-700">대기열 진입 중... 재고 확인 중</p>
          </div>
        )}

        {(status === 'SUCCESS' || status === 'ERROR') && (
          <div className={`p-8 rounded-2xl ${status === 'SUCCESS' ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'}`}>
            <p className="text-5xl mb-6">{status === 'SUCCESS' ? '🎉' : '😢'}</p>
            <p className={`text-xl font-black mb-6 ${status === 'SUCCESS' ? 'text-green-800' : 'text-red-800'}`}>
              {resultMsg}
            </p>
            <button
              onClick={() => setStatus('IDLE')}
              className="w-full py-3 bg-gray-800 text-white font-bold rounded-xl hover:bg-gray-700 transition active:scale-95"
            >
              다시 시도하기
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
