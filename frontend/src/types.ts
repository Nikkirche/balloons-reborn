export interface InfoHolder {
    token: string | null,
    setToken: (token: string | null) => void,
    info: Info,
    fetchInfo: () => Promise<void>
}

export interface Info {
    contestName: string
    canRegister?: boolean
    login?: string
    canAccess?: boolean
    canManage?: boolean,
    status: string
}