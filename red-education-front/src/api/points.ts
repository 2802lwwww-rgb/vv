import request from '@/utils/request'

// 获取积分商品列表
export function getPointsGoods(params: any) {
    return request({
        url: '/points/goods',
        method: 'get',
        params
    })
}

// 获取商品详情
export function getGoodsDetail(id: number) {
    return request({
        url: `/points/goods/${id}`,
        method: 'get'
    })
}

// 兑换商品
export function exchangeGoods(goodsId: number) {
    return request({
        url: '/points/exchange',
        method: 'post',
        data: { productId: goodsId }
    })
}

// 获取兑换记录
export function getExchangeRecords(params: any) {
    return request({
        url: '/points/exchange/records',
        method: 'get',
        params
    })
}

// 获取积分明细
export function getPointsHistory(params: any) {
    return request({
        url: '/points/history',
        method: 'get',
        params
    })
}

// 获取积分余额
export function getPointsBalance() {
    return request({
        url: '/points/balance',
        method: 'get'
    })
}

// 获取积分排行榜
export function getPointsRanking(params: any) {
    return request({
        url: '/points/ranking',
        method: 'get',
        params
    })
}
