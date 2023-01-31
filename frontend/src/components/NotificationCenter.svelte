<script>
    import mock_data from "../mock_data.js";
    import {Button} from "flowbite-svelte";
    import { fade } from 'svelte/transition';

    let notifications = mock_data.notification;

    let show = false;

    //TODO : Implement Buttons
    function acceptButton(n) {
        notifications = notifications.filter(item => item !== n)
    }

    function declineButton(n) {
        notifications = notifications.filter(item => item !== n)
    }

    function declineAllButton() {
        notifications = []
    }

    document.addEventListener(
        "click",
        function(event) {
            // If user either clicks outside the popup window, then close popup
            if (
                !event.target.closest(".modal")
            ) {
                show = false            }
        },
        false
    )
</script>

<!-- notification bell -->
<div>
    <Button class="relative p-1 modal" pill color="alternative" on:click={() => (show = !show)}>
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
        <span class="inline-block absolute top-0 bg-blue-500 text-white right-0 rounded-full text-xs min-w-[18px] min-h-[18px] text-center">{notifications.length}</span>
    </Button>
</div>
{#if show}
    <!-- clicking anywhere on the page will close the popup -->

    <!-- popup itself -->
    <div
            class="absolute z-50 top-[60px] right-[170px] p-3 mt-1 text-gray-600 bg-white bg-gray-100 rounded
            shadow-md overflow-y-auto max-h-[235px] w-[512px] text-center modal"
    >
        <ul class="space-y-3">
            {#each notifications as n}
                <li class="p-3 border rounded relative" transition:fade>
                    <p class="font-bold">{n.subject}</p>
                    <p>{n.message}</p>
                    <div class="mt-1">

                        <Button class="px-2 rounded-lg text-sm text-blue-200 bg-blue-700 rounded-sm" on:click={() => acceptButton(n)}>
                            Accept
                        </Button>
                        <Button class="px-2 rounded-lg text-sm text-red-200 bg-red-700 rounded-sm" on:click={() => declineButton(n)}>
                            Decline
                        </Button>
                    </div>
                </li>
            {/each}
        </ul>
        <div class="flex justify-center mt-3">
            {#if notifications.length !== 0}
                <Button class="px-2 rounded-lg text-sm text-red-200 bg-red-700 rounded-sm" on:click={() => declineAllButton()}>
                    Decline All
                </Button>
            {:else}
                No Notifications
            {/if}
        </div>
    </div>
{/if}
