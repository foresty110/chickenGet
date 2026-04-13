import { GachaResponse } from '../types';

const API_BASE_URL = 'http://localhost:8080/api/v1';

export const drawGacha = async (userId: number): Promise<GachaResponse<string>> => {
  const response = await fetch(`${API_BASE_URL}/gacha/draw?userId=${userId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('서버 응답에 실패했습니다.');
  }

  return response.json();
};
