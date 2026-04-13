'use client';

import { useState } from 'react';
import { drawGacha } from '@/services/api';
import { GachaStatus } from '@/types';

export default function GachaPage() {
  const [status, setStatus] = useState<GachaStatus>('IDLE');
  const [resultMsg, setResultMsg] = useState('');

  const handleDraw = async () => {
    setStatus('LOADING');
    try {
      const response = await drawGacha(1); 
      if (response.success) {
        setStatus('SUCCESS');
        setResultMsg(response.data || '축하합니다!');
      } else {
        setStatus('ERROR');
        setResultMsg(response.message);
      }
    } catch (error) {
      setStatus('ERROR');
      setResultMsg('통신 중 오류가 발생했습니다.');
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-4">
      <div className="bg-white p-8 rounded-2xl shadow-2xl w-full max-w-md text-center">
        <h1 className="text-3xl font-bold text-orange-600 mb-6">🍗 치킨 가챠 시스템</h1>
        
        {status === 'IDLE' && (
          <button
            onClick={handleDraw}
            className="w-full py-4 bg-orange-500 hover:bg-orange-600 text-white font-bold rounded-xl transition-all"
          >
            쿠폰 뽑기 도전!
          </button>
        )}

        {status === 'LOADING' && (
          <div className="space-y-4">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500 mx-auto"></div>
            <p className="text-blue-600 font-medium">대기열 진입 중... 재고를 확인하고 있습니다.</p>
          </div>
        )}

        {(status === 'SUCCESS' || status === 'ERROR') && (
          <div className={`p-6 rounded-xl ${status === 'SUCCESS' ? 'bg-green-50' : 'bg-red-50'}`}>
            <p className="text-4xl mb-4">{status === 'SUCCESS' ? '🎉' : '😢'}</p>
            <p className={`text-lg font-bold mb-4 ${status === 'SUCCESS' ? 'text-green-800' : 'text-red-800'}`}>
              {resultMsg}
            </p>
            <button
              onClick={() => setStatus('IDLE')}
              className="px-6 py-2 bg-gray-800 text-white rounded-lg hover:bg-gray-700 transition"
            >
              다시 시도
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
