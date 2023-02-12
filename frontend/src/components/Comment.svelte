<script>
    import ConfirmDeletionModal from "./ConfirmDeletionModal.svelte";
    import {Button} from "flowbite-svelte";
    import {onMount} from 'svelte'

    export let comment = null;
    export let error = null;

    export let review;
    export let category;

    let path = ""
    let delete_self_path = ""

    export let show_delete = false;
    let text = comment.content;

    let editable = false;
    let commentedTime = new Date;
    //TODO UserController
    let user = null;

    let current = new Date()
    onMount(() => {
        setTimeout(function () {
            current = new Date();
        }, 60000);
        commentedTime = new Date(Date.parse(comment.timestamp))
    })

    $: calculateDate = (date) => {
        date = new Date(date);

        // To calculate the time difference of two dates
        let diff = current.getTime() - date.getTime();

        // Calculate difference between the date and now
        let diffDay = (diff / (1000 * 3600 * 24))
        let diffHour = (Math.abs(diff) / 36e5)
        let diffMin = (Math.round((diff / 1000) / 60))
        console.log(diffDay, diffHour, diffMin)

        //return the correct string
        if (diffDay < 0) {
            return Math.round(diffDay) + " days ago"
        }else if (diffDay >= 1 && diffDay < 2) {
            return Math.round(diffDay) + " day ago"
        } else if (diffDay > 1) {
            return Math.round(diffDay) + " days ago"
        }
        if (diffHour >= 1 && diffHour < 2) {
            return Math.round(diffDay) + " hour ago"
        } else if (diffHour > 1) {
            return Math.round(diffHour) + " hours ago"
        }
        if (diffMin >= 1 && diffMin < 2) {
            return Math.round(diffMin) + " minute ago"
        } else if (diffMin > 1) {
            return Math.round(diffMin) + " minutes ago"
        }
        return "just now"
    }

    function editComment() {
        editable = !editable;
        let data = {
            id: comment.id,
            content: text
        }
        fetch("/api" + path + "/messages", {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data),
        })
            .then((response) => response.json())
            .then((response) => {
                if (response.status < 200 || response.status >= 300) {
                    error = "" + response.status + ": " + response.message;
                    console.log(error);
                } else {
                    console.log("Save success")
                }
            })
            .catch(err => console.log(err))
    }


    onMount(() => {
        path = "/categories/" + category.id + "/entries/" + review.entry_id + "/reviews/" + review.id
        delete_self_path = path + "/messages/" + comment.id;
    })

</script>


<main>
    <div class="rounded-lg outline outline-blue-500 mx-4 my-4 max-w-[90vw]">
        <div class="h-1"/>
        {#if comment !== null}
            <div class="font-bold flex mx-2 w-full flex justify-between">
                {user}
                <div class="flex justify-end">
                    <Button pill class="!p-2" outline on:click={() => editable = true}>
                        <svg class="svg-icon w-3 h-3" viewBox="0 0 20 20">
                            <path d="M18.303,4.742l-1.454-1.455c-0.171-0.171-0.475-0.171-0.646,0l-3.061,3.064H2.019c-0.251,
                        0-0.457,0.205-0.457,0.456v9.578c0,0.251,0.206,0.456,0.457,0.456h13.683c0.252,0,0.457-0.205,
                        0.457-0.456V7.533l2.144-2.146C18.481,5.208,18.483,4.917,18.303,4.742 M15.258,
                        15.929H2.476V7.263h9.754L9.695,9.792c-0.057,0.057-0.101,0.13-0.119,0.212L9.18,11.36h-3.98c-0.251,
                        0-0.457,0.205-0.457,0.456c0,0.253,0.205,0.456,0.457,0.456h4.336c0.023,0,0.899,0.02,
                        1.498-0.127c0.312-0.077,0.55-0.137,0.55-0.137c0.08-0.018,0.155-0.059,
                        0.212-0.118l3.463-3.443V15.929z M11.241,11.156l-1.078,0.267l0.267-1.076l6.097-6.091l0.808,
                        0.808L11.241,11.156z">
                            </path>
                        </svg>
                    </Button>
                    <Button pill class="!p-2 mx-3 " outline color="red"
                            on:click={() => show_delete = true}>
                        <svg class="w-3 h-3" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"
                             width="32px" height="32px" viewBox="0 0 64 64"
                             xml:space="preserve">
                          <g>
                            <line fill="none" stroke="#000000" stroke-width="4" stroke-miterlimit="10" x1="8.947"
                                  y1="7.153" x2="55.045"
                                  y2="53.056"/>
                          </g>
                            <g>
                            <line fill="none" stroke="#000000" stroke-width="4" stroke-miterlimit="10" x1="9.045"
                                  y1="53.153" x2="54.947"
                                  y2="7.056"/>
                          </g>
                      </svg>
                    </Button>
                </div>
            </div>
            <h1 class="text-xs mx-2 my-2">{calculateDate(commentedTime)}</h1>
            {#if !editable}
                <input bind:value={text}
                       class="my-2.5 font-normal text-gray-700 mx-2 border-none" type=text disabled>
            {:else }
                <form on:submit={() => editComment()}>
                    <input bind:value={text}
                           class="my-2.5 font-normal text-gray-700 mx-2 rounded-lg relative w-[98%]" type=text>
                </form>
            {/if}
        {/if}
        <div class="h-1"/>
    </div>
</main>

<ConfirmDeletionModal hide="{() => show_delete = false}" show="{show_delete}"
                      to_delete={delete_self_path} delete_name="comment" afterpath= {path}/>