import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { localModsApi } from '../api/client';

export function useSyncLocalMods() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => localModsApi.syncLocalMods(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.localModSyncStatus() });
    },
  });
}

export function useLocalModSyncStatus(enabled: boolean) {
  return useQuery({
    queryKey: queryKeys.localModSyncStatus(),
    queryFn: async () => (await localModsApi.getLocalModSyncStatus()).data,
    refetchInterval: enabled ? 2000 : false,
    refetchIntervalInBackground: true,
  });
}

const queryKeys = {
  localModSyncStatus: () => ['localModSyncStatus'] as const,
};
