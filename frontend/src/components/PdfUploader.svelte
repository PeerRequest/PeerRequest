<script>
  import { Button, Fileupload, Spinner } from "flowbite-svelte";

  let upload_state = "";
  let annotations_file = null;
  let fileuploadprops = {
    id: "annotations_file_input",
    accept: ".pdf,application/pdf"
  };

  function file_upload() {
    if (upload_state === "uploading" || upload_state === "done") return;

    const input = document.getElementById(fileuploadprops.id);
    const file = input.files[0];

    // upload file here
    console.log(file);

    upload_state = "uploading";
    setTimeout(() => {
      upload_state = Math.random() < 0.5 ? "failed" : "done";
    }, 2200);
  }
</script>

<div class="flex flex-1 items-center ">
  <Fileupload {...fileuploadprops} bind:value={annotations_file} inputClass="my-auto annotations_file_input"
              on:change={() => upload_state = ""}
              size="lg"
  />
  <Button class="m-auto" color={upload_state === "done" ? "green" : (upload_state === "failed" ? "red" : "blue")}
          disabled={!annotations_file} on:click={file_upload}
          outline size="md">
    {#if upload_state === "uploading"}
      <Spinner class="mr-3" size="5" />
    {:else }
      {#if upload_state === "done"}
        <svg class="mr-3" width="15px" height="15px" viewBox="0 0 15 15" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M1 7L5.5 11.5L14 3" stroke="black" stroke-linecap="square" />
        </svg>
      {:else }
        {#if upload_state === "failed" }
          <svg class="mr-3" width="15px" height="15px" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"
               viewBox="0 0 490 490" style="enable-background:new 0 0 490 490;" xml:space="preserve">
                <polygon points="456.851,0 245,212.564 33.149,0 0.708,32.337 212.669,245.004 0.708,457.678 33.149,490 245,277.443 456.851,490
                  489.292,457.678 277.331,245.004 489.292,32.337 " />
          </svg>
        {/if}
      {/if}
    {/if}{upload_state === "done" ? "Done" : (upload_state === "failed" ? "Failed" : "Upload")}
  </Button>
</div>