<script>
    import mock_data from "../mock_data.js";


</script>

<style>
    /* unread message count */
    .badge {
        display: inline-block;
        position: absolute;
        top: 0;
        background-color: #4285f4;
        color: #d7e6fd;
        right: 0;
        border-radius: 9999px;
        font-size: 12px;
        min-width: 18px;
        line-height: 18px;
        min-height: 18px;
        text-align: center;
    }
</style>

<button class="relative p-1">
    <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="w-6 h-6"
    >
        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
        <path d="M13.73 21a2 2 0 0 1-3.46 0" />
    </svg>
    <span class="badge">2</span>
</button>