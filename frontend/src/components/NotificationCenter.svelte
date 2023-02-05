<script>
    import mock_data from "../mock_data.js";
    import {Button} from "flowbite-svelte";
    import { fade } from 'svelte/transition';
    import { onMount } from 'svelte';

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

    onMount(() => {
        document.addEventListener(
            "click",
            function(event) {
                // If user either clicks outside the popup window, then close popup
                if (
                    !event.target.closest(".modal")
                ) {
                    show = false
                }
            },
            false
        )
    })
</script>

<!-- notification bell -->
<div class="mb-2">
    <Button class="relative p-1 modal border-none rounded-full" pill color="alternative" on:click={() => (show = !show)}>
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
        <span class="inline-block absolute top-1 bg-blue-500 text-white right-3 rounded-full text-xs min-w-[18px] min-h-[18px] text-center">{notifications.length}</span>
    </Button>
</div>
{#if show}
    <!-- popup itself -->
    <div
            class="absolute z-50 top-[8vh] right-0 p-3 mt-1 text-gray-600 bg-white rounded
            shadow-md overflow-y-auto max-h-[35vh] w-[40vw] text-left modal"
    >
        <ul class="space-y-3">
            {#each notifications as n}
                <li class="p-3 border rounded relative outline outline-gray-100 flex gap-x-4 grid-rows-1" transition:fade>
                    <div>
                        <p class="font-bold">{n.subject}</p>
                        <p class="w-fit h-fit">{n.message}</p>
                    </div>
                    <div class="mt-2 flex justify-end gap-x-4 w-full h-fit">
                        <div class="flex flex-wrap items-center gap-2 grid gap-y-3">

                            <Button pill class="!p-2" outline
                                    on:click={() => acceptButton(n)}>
                                <svg class="w-5 h-5" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"
                                     width="32px" height="32px" viewBox="0 0 27 27" enable-background="new 0 0 64 64"
                                     xml:space="preserve"
                                     fill="none" stroke="#000000" stroke-width="2" stroke-miterlimit="10">
                                    <polyline points="20 6 9 17 4 12"></polyline>
                                </svg>
                            </Button>
                            <Button pill class="!p-2" outline color="red"
                                             on:click={() => declineButton(n)}>
                            <svg class="w-5 h-5" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"
                                 width="32px" height="32px" viewBox="0 0 64 64" enable-background="new 0 0 64 64"
                                 xml:space="preserve">
                                    <g>
                                        <line fill="none" stroke="#000000" stroke-width="2" stroke-miterlimit="10" x1="18.947"
                                              y1="17.153" x2="45.045"
                                              y2="43.056" />
                                    </g>
                                <g>
                                        <line fill="none" stroke="#000000" stroke-width="2" stroke-miterlimit="10" x1="19.045"
                                              y1="43.153" x2="44.947"
                                              y2="17.056" />
                                    </g>
                                </svg>
                        </Button>
                        </div>
                    </div>
                </li>
            {/each}
        </ul>
        <div class="flex justify-center mt-3">
            {#if notifications.length !== 0}
                <Button class="px-2 rounded-lg text-sm " outline color="red" on:click={() => declineAllButton()}>
                    Decline All
                </Button>
            {:else}
                No Notifications
            {/if}
        </div>
    </div>
{/if}
