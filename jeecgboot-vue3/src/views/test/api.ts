    import {defHttp} from '/@/utils/http/axios';
    import {Modal} from 'ant-design-vue';

    enum Api {
        chat = '/deepSeekApi/chat',
    }

    /**
     * 列表接口
     * @param params
     */
    export const list = (params) =>
    defHttp.get({url: Api.chat, params});

    export const chat = async (message: string) => {
        try {
            const response = await defHttp.post({
                url: Api.chat, // 发送 POST 请求
                params: { message }, // 将 message 作为请求体中的 JSON 数据
            });
            return response; // 返回 AI 的回复
        } catch (error) {
            console.error('API Error:', error);
            throw error; // 抛出错误，由调用方处理
        }
    };
