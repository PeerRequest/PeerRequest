<script>
    import mock_data from "../mock_data.js";

    let notifiactions = mock_data.notification;

    let show = false;
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

<button class="relative p-1" on:click={() => (show = !show)}>
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
    <span class="badge">{notifiactions.length}</span>
</button>

{#if show}
    <!-- clicking anywhere on the page will close the popup -->
    <button
            tabindex="-1"
            class="fixed inset-0 w-full h-full cursor-default focus:outline-none"
            on:click|preventDefault={() => (show = false)}
    />

    <div
            class="absolute z-50 top-[50px] right-[170px] p-3 mt-1 text-gray-600 bg-white bg-gray-100 rounded shadow-md messages lg:overflow-visible"
    >
        <ul class="space-y-3">
            {#each notifiactions as n}
                <li class="p-3 border rounded">
                    <p>{n.message}</p>
                    <div class="mt-1">
                        <button class="px-2 text-sm text-blue-200 bg-blue-700 rounded-sm" on:click={() => notifiactions = notifiactions.filter(item => item !== n)}>
                            Ignore
                        </button>
                    </div>
                </li>
            {/each}
        </ul>
        <div class="flex justify-end mt-3">
            {#if notifiactions.length !== 0}
                <button class="px-2 text-sm text-blue-200 bg-blue-700 rounded-sm" on:click={() => notifiactions = []}>
                    Ignore All
                </button>
            {:else}
                No Notifications
            {/if}
        </div>
    </div>
{/if}
